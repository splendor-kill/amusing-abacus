package com.tentacle.callofwild.gmaster;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.tentacle.callofwild.protocol.MyCodec;
import com.tentacle.callofwild.protocol.MyCodec.Cocoon;
import com.tentacle.callofwild.protocol.ProtoAdmin.SysCommonReq;
import com.tentacle.callofwild.protocol.ProtoAdmin.Warrant;
import com.tentacle.callofwild.protocol.ProtoBasis.Instruction;
import com.tentacle.callofwild.protocol.ProtoBasis.eCommand;
import com.tentacle.callofwild.util.Consts;
import com.tentacle.callofwild.util.Utils;

public class SafeQuit {
	private static final Logger logger = Logger.getLogger(SafeQuit.class);
	
	public static void quitSafely(String adminName, String adminKey, String ipv4, int port) throws IOException {		
		final int req_timeout_duration = 60 * 1000; // by millisecond

		Socket socket = new Socket();
		socket.setTcpNoDelay(true);
		socket.setSoTimeout(req_timeout_duration);
		socket.connect(new InetSocketAddress(ipv4, port));
		
		Instruction ins = Instruction.newBuilder().setCmd(eCommand.SYS_EXIT).setId(0l).build();
		SysCommonReq.Builder req = SysCommonReq.newBuilder().setCmd(ins)
				.setProof(Warrant.newBuilder().setAdminName(adminName).setCachet(adminKey));
		
		Cocoon cocoon = new Cocoon(ins.getCmd().getNumber(), ins.getId(), req.build().toByteArray());		
		MyCodec.encode(cocoon, socket.getOutputStream());
		
		socket.close();
	}

	public static void main(String[] args) {
		PropertyConfigurator.configure(Consts.LOG_FILE_PATH);
		if (args.length < 2) {
			System.out.println("usage: SafeQuit name key port");
			return;
		}
		String adminName = args[0];
		String adminKey = args[1];
		String ipv4 = "localhost";
		String tmp;
		if (args.length == 3) {
		    tmp = args[2];
		} else {
		    tmp = Utils.getConfig().getProperty("game_server.listening_port", "10000");
		}
		
		try {
            int port = Integer.parseInt(tmp);
			quitSafely(adminName, adminKey, ipv4, port);
			String fingerprint = "[" + adminName + "] issue the 'EXIT' command at " + (new Date()) + ".";
			logger.info(fingerprint);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

}
