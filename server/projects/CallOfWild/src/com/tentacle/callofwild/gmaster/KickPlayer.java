package com.tentacle.callofwild.gmaster;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.tentacle.callofwild.protocol.MyCodec;
import com.tentacle.callofwild.protocol.MyCodec.Cocoon;
import com.tentacle.callofwild.protocol.ProtoAdmin.SysLimitPlayerBehavior;
import com.tentacle.callofwild.protocol.ProtoAdmin.Warrant;
import com.tentacle.callofwild.protocol.ProtoBasis.Instruction;
import com.tentacle.callofwild.protocol.ProtoBasis.eCommand;
import com.tentacle.callofwild.util.Utils;

public class KickPlayer {
	private static final Logger logger = Logger.getLogger(KickPlayer.class);

	public static void kickPlayer(String adminName, String adminKey, String memo, long playerId, long span) throws IOException {
		final int req_timeout_duration = 60 * 1000; // by millisecond
		
		String ipv4 = Utils.getConfig().getProperty("game_server.ipv4", "127.0.0.1");		
		String tmp = Utils.getConfig().getProperty("game_server.listening_port", "10000");
		int port = Integer.parseInt(tmp);
		
		Socket socket = new Socket();
		socket.setTcpNoDelay(true);
		socket.setSoTimeout(req_timeout_duration);
		socket.connect(new InetSocketAddress(ipv4, port));
		
		long now = System.currentTimeMillis();
		Instruction ins = Instruction.newBuilder().setCmd(eCommand.SYS_KICK_PLAYER).setId(0l).build();
		SysLimitPlayerBehavior.Builder req = SysLimitPlayerBehavior.newBuilder()
				.setCmd(ins)
				.setProof(Warrant.newBuilder().setAdminName(adminName).setCachet(adminKey))
				.setPlayerId(playerId)
				.setBeginBanTime(now)
				.setEndBanTime(now + span)
				.setMemo(memo)
				.setSupervisor(adminName);
		
		Cocoon cocoon = new Cocoon(ins.getCmd().getNumber(), ins.getId(), req.build().toByteArray());		
		MyCodec.encode(cocoon, socket.getOutputStream());
	}

	public static void main(String[] args) {
//		PropertyConfigurator.configure(Consts.LOG_FILE_PATH);
		if (args.length < 5) {
			System.out.println("usage: KickPlayer name key player timespan memo");
			return;
		}
		String adminName = args[0];
		String adminKey = args[1];
		long playerId = Long.parseLong(args[2]);
		long timespan = Long.parseLong(args[3]);
		String memo = args[4];
		
		
		try {
			kickPlayer(adminName, adminKey, memo, playerId, timespan);
			String fingerprint = "[" + adminName + "] kick the player[" + playerId + "].";
			System.out.println(fingerprint);
			logger.info(fingerprint);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
		}

	}

}
