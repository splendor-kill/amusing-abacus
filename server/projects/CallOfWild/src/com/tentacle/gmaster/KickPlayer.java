package com.tentacle.gmaster;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.tentacle.common.protocol.MyCodec;
import com.tentacle.common.protocol.MyCodec.Cocoon;
import com.tentacle.common.protocol.ProtoAdmin.SysLimitPlayerBehavior;
import com.tentacle.common.protocol.ProtoAdmin.Warrant;
import com.tentacle.common.protocol.ProtoBasis.Instruction;
import com.tentacle.common.protocol.ProtoBasis.eCommand;

public class KickPlayer {
	private static final Logger logger = Logger.getLogger(KickPlayer.class);

	public static void kickPlayer(String adminName, String adminKey, String memo, long playerId, long span, String ipv4, int port) throws IOException {
		final int req_timeout_duration = 60 * 1000; // by millisecond

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
		if (args.length < 7) {
			System.out.println("usage: KickPlayer name key player timespan memo ip port");
			return;
		}
		String adminName = args[0];
		String adminKey = args[1];
		long playerId = Long.parseLong(args[2]);
		long timespan = Long.parseLong(args[3]);
		String memo = args[4];
		String ip = args[5];
		int port = Integer.parseInt(args[6]);
		
		
		try {
			kickPlayer(adminName, adminKey, memo, playerId, timespan, ip, port);
			String fingerprint = "[" + adminName + "] kick the player[" + playerId + "].";
			System.out.println(fingerprint);
			logger.info(fingerprint);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e);
		}

	}

}
