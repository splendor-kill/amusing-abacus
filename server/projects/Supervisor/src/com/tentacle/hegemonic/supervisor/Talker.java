package com.tentacle.hegemonic.supervisor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tentacle.trickraft.protocol.MyCodec;
import com.tentacle.trickraft.protocol.MyCodec.Cocoon;
import com.tentacle.trickraft.protocol.ProtoAdmin.SysGmTalk;
import com.tentacle.trickraft.protocol.ProtoAdmin.SysGmTalkAns;
import com.tentacle.trickraft.protocol.ProtoAdmin.Warrant;
import com.tentacle.trickraft.protocol.ProtoBasis.Instruction;
import com.tentacle.trickraft.protocol.ProtoBasis.eChatObject;
import com.tentacle.trickraft.protocol.ProtoBasis.eCommand;
import com.tentacle.trickraft.protocol.ProtoPlayer.ChatData;


class Receiver implements Runnable {
	
	private Socket socket;
	private LinkedBlockingQueue<Cocoon> recvQ;
	
	public Receiver(Socket socket, LinkedBlockingQueue<Cocoon> recvQ) {
		this.socket = socket;
		this.recvQ = recvQ;
	}

	@Override
	public void run() {
		assert socket.isConnected();
		
		while (true) {
			try {
				Cocoon ans = MyCodec.decode(socket.getInputStream());
				if (ans != null)
					recvQ.put(ans);
				Thread.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}


public class Talker {
	private final static int req_timeout_duration	= 60 * 1000; //by millisecond
	private final static String cfg_file = "res/config.properties";
	public final static String prompt = "> ";
	
	private Socket socket = new Socket();
	private String ipv4;
	private int port;
	private String adminName;
	private String adminKey;
	private String gameMaster;
	private long gmId;
			
	private LinkedBlockingQueue<Cocoon> recvQ = new LinkedBlockingQueue<Cocoon>();
	private long sayingCounter;

	private void load() throws FileNotFoundException, IOException {
		Properties p = new Properties();
		p.load(new FileInputStream(cfg_file));
		
		ipv4 = p.getProperty("game_server.ipv4", "127.0.0.1");
		String tmp = p.getProperty("game_server.listening_port", "57082");//DEFAult port
		port = Integer.parseInt(tmp);
		adminName = p.getProperty("admin_name", "");
		adminKey = p.getProperty("admin_key", "");
		gameMaster = p.getProperty("game_master_player_name", "");
	}
	
	public void init() throws FileNotFoundException, IOException {
		load();
		
		socket = new Socket();
		socket.setTcpNoDelay(true);
		socket.setSoTimeout(req_timeout_duration);
		socket.connect(new InetSocketAddress(ipv4, port));
	}
	
	public void join() throws IOException {
		Instruction ins = Instruction.newBuilder().setCmd(eCommand.SYS_GM_TALK).setId(0l).build();
		SysGmTalk.Builder req = SysGmTalk.newBuilder().setCmd(ins)
				.setProof(Warrant.newBuilder().setAdminName(adminName).setCachet(adminKey)).setGameMaster(gameMaster);
		Cocoon cocoon = new Cocoon(ins.getCmd().getNumber(), ins.getId(), req.build().toByteArray());
		MyCodec.encode(cocoon, socket.getOutputStream());
	}
	
	public void dataComeOn() {
		new Thread(new Receiver(socket, recvQ)).start();
	}

	private void recvChatData(ChatData dat) {
		System.out.println("[" + dat.getFromName() + "]: " + dat.getMsg());
	}
	
	private void recvGmTalkAns(SysGmTalkAns dat) {
		gmId = dat.getGameMasterId();
		System.out.println("["+gameMaster+"]'s id is ["+gmId+"]");
	}
	
	public void passive() throws InvalidProtocolBufferException {
		while (!recvQ.isEmpty()) {
			Cocoon ans = recvQ.poll();
			eCommand cmd = eCommand.valueOf(ans.cmdType);
			if (cmd == null)
				continue;

			switch (cmd) {
			case PUSH_CHAT_CONTENT:
				recvChatData(ChatData.parseFrom(ans.dat));
				break;
			case SYS_GM_TALK:
				recvGmTalkAns(SysGmTalkAns.parseFrom(ans.dat));
				break;
			default:
				;
			}
		}
	}
	
	public void say(String content) {
		Instruction ins = Instruction.newBuilder().setCmd(eCommand.CHAT).setId(sayingCounter++).build();
		ChatData.Builder req = ChatData.newBuilder().setCmd(ins)
				.setFromName(gameMaster)
				.setObject(eChatObject.CHT_WORLD)
				.setTime(System.currentTimeMillis())
				.setMsg(content);
		Cocoon cocoon = new Cocoon(ins.getCmd().getNumber(), ins.getId(), req.build().toByteArray());
		try {
			MyCodec.encode(cocoon, socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	
	public static void main(String[] args) throws Exception {
		Talker talker = new Talker();

		talker.init();
		talker.join();
		
		Thread.sleep(1000);
		
		talker.dataComeOn();

		String line;
		
		Scanner scanIn = new Scanner(System.in);
		
		do {
			talker.passive();
			if (!scanIn.hasNext())
				continue;
			
			System.out.print(prompt);

			line = scanIn.nextLine();
			if (line == null) {
				continue;
			}
			
			line = line.trim();
			if (line.isEmpty())
				continue;
			if (line.equals("quit"))
				break;
			talker.say(line);

		} while (true);
		
		System.out.println("bye.");
	}

}
