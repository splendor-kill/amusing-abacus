package com.tentacle.hegemonic.supervisor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Properties;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tentacle.trickraft.protocol.MyCodec;
import com.tentacle.trickraft.protocol.MyCodec.Cocoon;
import com.tentacle.trickraft.protocol.ProtoAdmin.SysCommonReq;
import com.tentacle.trickraft.protocol.ProtoAdmin.SysQueryDeadAns;
import com.tentacle.trickraft.protocol.ProtoAdmin.Warrant;
import com.tentacle.trickraft.protocol.ProtoBasis.Instruction;
import com.tentacle.trickraft.protocol.ProtoBasis.eCommand;
import com.tentacle.trickraft.protocol.ProtoBasis.eErrorCode;

public class Starter {
	private final static int req_timeout_duration	= 60 * 1000; //by millisecond
	
	private Socket socket = new Socket();
	String ipv4;
	int port;

	
	public void init() throws FileNotFoundException, IOException {
		Properties p = new Properties();
		p.load(new FileInputStream("res/config.properties"));
		String tmp = p.getProperty("game_server.ipv4", "127.0.0.1");
		ipv4 = tmp;
		tmp = p.getProperty("game_server.listening_port", "9090");
		port = Integer.parseInt(tmp);
		socket = new Socket();
		socket.setTcpNoDelay(true);
		socket.setSoTimeout(req_timeout_duration);
		socket.connect(new InetSocketAddress(ipv4, port));
	}
		
	public void reqAskIfDead() throws IOException {
		Instruction ins = Instruction.newBuilder().setCmd(eCommand.SYS_IF_SERVER_DEAD).setId(0l).build();
		SysCommonReq.Builder req = SysCommonReq.newBuilder()
				.setCmd(ins)
				.setProof(Warrant.newBuilder().setAdminName("admin").setCachet("d41d8cd98f00b204e9800998ecf8427e"));
		Cocoon cocoon = new Cocoon(ins.getCmd().getNumber(), ins.getId(), req.build().toByteArray());
		MyCodec.encode(cocoon, socket.getOutputStream());
	}
	
	public int procAskIfDead() {
		Cocoon ans = null;
		try {
			ans = MyCodec.decode(socket.getInputStream());
		} catch (IOException e) {
			System.exit(-2);
		}
		if (ans == null)
			System.exit(-2);
		
		if (ans.cmdType != eCommand.SYS_IF_SERVER_DEAD_VALUE)
			System.exit(-2);
		
		SysQueryDeadAns result = null;
		try {
			result = SysQueryDeadAns.parseFrom(ans.dat);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
			System.exit(-2);
		}
		if (result.getErrCode() != eErrorCode.OK)
			System.exit(-2);
		
		return result.getCurSizeMsgQueue();		
	}

	public static void main(String[] args) {
		Starter starter = new Starter();
		try {
			starter.init();
			starter.reqAskIfDead();
		} catch (Exception e) {
			System.exit(-1);
		}
		
		try {
			Thread.sleep(req_timeout_duration);
		} catch (InterruptedException e) {
		}
		
		int status = starter.procAskIfDead();
		
		System.exit(status);
	}

}
