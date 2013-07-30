package com.tentacle.hegemonic.supervisor;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;

import com.tentacle.trickraft.protocol.MyCodec;
import com.tentacle.trickraft.protocol.MyCodec.Cocoon;
import com.tentacle.trickraft.protocol.ProtoAdmin.SysCommonReq;
import com.tentacle.trickraft.protocol.ProtoAdmin.SysLimitPlayerBehavior;
import com.tentacle.trickraft.protocol.ProtoAdmin.Warrant;
import com.tentacle.trickraft.protocol.ProtoBasis.Instruction;
import com.tentacle.trickraft.protocol.ProtoBasis.eCommand;


interface Handler {
	void exec(Director context, String param) throws Exception;
}

class HandlerSysPost implements Handler {

	@Override
	public void exec(Director context, String param) {
		
	}	
}


class HandlerIfServerDead implements Handler {
	
	@Override
	public void exec(Director context, String param) {
	}
}


class HandlerSafeQuit implements Handler {
	
	@Override
	public void exec(Director context, String param) throws IOException {
		Instruction ins = Instruction.newBuilder().setCmd(eCommand.SYS_EXIT).setId(0l).build();
		SysCommonReq.Builder req = SysCommonReq.newBuilder()
				.setCmd(ins)
				.setProof(Warrant.newBuilder().setAdminName("admin").setCachet("d41d8cd98f00b204e9800998ecf8427e"));
		Cocoon cocoon = new Cocoon(ins.getCmd().getNumber(), ins.getId(), req.build().toByteArray());
		if (context.socket.isClosed() || !context.socket.isConnected())
			context.connect();
		try {
			MyCodec.encode(cocoon, context.socket.getOutputStream());
		} catch (SocketException e) {
			context.socket.close();
			context.connect();
			MyCodec.encode(cocoon, context.socket.getOutputStream());
		}		
	}
}


class HandlerKick implements Handler {
	
	@Override
	public void exec(Director context, String param) throws IOException {
		long playerId = 0;
		long now = System.currentTimeMillis();
		Instruction ins = Instruction.newBuilder().setCmd(eCommand.SYS_KICK_PLAYER).setId(0l).build();
		SysLimitPlayerBehavior.Builder req = SysLimitPlayerBehavior.newBuilder()
				.setCmd(ins)
				.setProof(Warrant.newBuilder().setAdminName("admin").setCachet("d41d8cd98f00b204e9800998ecf8427e"))
				.setPlayerId(playerId)
				.setBeginBanTime(now)
				.setEndBanTime(now + 1000 * 60 * 2)
				.setMemo("kick off!")
				.setSupervisor("admin");
		Cocoon cocoon = new Cocoon(ins.getCmd().getNumber(), ins.getId(), req.build().toByteArray());
		if (context.socket.isClosed() || !context.socket.isConnected())
			context.connect();
		try {
			MyCodec.encode(cocoon, context.socket.getOutputStream());
		} catch (SocketException e) {
			context.socket.close();
			context.connect();
			MyCodec.encode(cocoon, context.socket.getOutputStream());
		}		
	}
}



public class Director {
	static final String prompt = "> ";
	
	static final String ask_reload_cfg = "reload-config:";
	static final String ask_sys_post = "sys-post:";
	static final String ask_exit = "exit";
	static final String ask_query_online_players = "query-online-players:";
	static final String ask_present_gift = "present-gift:";
	static final String ask_if_server_dead = "if-server-dead:";
	static final String ask_kick_off = "kick:";
	
	static Map<String, Handler> tells = new HashMap<String, Handler>();
	
	static {
		tells.put(ask_reload_cfg, null);
		tells.put(ask_sys_post, null);
		tells.put(ask_exit, new HandlerSafeQuit());
		tells.put(ask_query_online_players, null);
		tells.put(ask_present_gift, null);
		tells.put(ask_if_server_dead, null);
		tells.put(ask_kick_off, new HandlerKick());
	}

	final static int req_timeout_duration = 60 * 1000; // by millisecond
	Socket socket;
	String ipv4;
	int port;

	public boolean init() {
		try {
			Properties p = new Properties();
			p.load(new FileInputStream("res/config.properties"));
			String tmp = p.getProperty("game_server.ipv4", "127.0.0.1");
			ipv4 = tmp;
			tmp = p.getProperty("game_server.listening_port", "9801");
			port = Integer.parseInt(tmp);
			socket = new Socket();
			socket.setTcpNoDelay(true);
			socket.setSoTimeout(req_timeout_duration);
			if (!connect()) 
				return false;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}
	
	public boolean connect() {
		try {
			socket.connect(new InetSocketAddress(ipv4, port));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	public static void main(String[] args) throws InterruptedException {
		String line;
		String params;
		
		Director boss = new Director();

		if (!boss.init())
			return;
		
		Scanner scanIn = new Scanner(System.in);

		do {
			System.out.print(prompt);

			line = scanIn.nextLine();
			if (line == null) {
				Thread.sleep(0l);
				continue;
			}
			
			System.out.println(line);
	
			line = line.trim();
			if (line.equals("quit"))
				break;

			for (Entry<String, Handler> i : tells.entrySet()) {
				params = i.getKey();
				if (!line.startsWith(params))
					continue;
				Handler h = i.getValue();
				if (h == null)
					continue;
				
				params = line.substring(params.length()).trim();
				try {
					h.exec(boss, params);
				} catch (Exception e) {
					System.out.println(i.getKey() + "\t" + e);
				}
			}

		} while (true);
		
		System.out.println("bye.");

	}

}
