package com.tentacle.gmaster;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.tentacle.common.protocol.MyCodec;
import com.tentacle.common.protocol.MyCodec.Cocoon;
import com.tentacle.common.protocol.ProtoAdmin.SysPwdReset;
import com.tentacle.common.protocol.ProtoAdmin.Warrant;
import com.tentacle.common.protocol.ProtoBasis.Instruction;
import com.tentacle.common.protocol.ProtoBasis.eCommand;

public class ResetPassword {
    private static final Logger logger = Logger.getLogger(ResetPassword.class);
    
    public static void reset(String adminName, String adminKey, 
            int userId, long playerId, String playerName, String newPwd, String ipv4, int port) throws IOException {
        final int req_timeout_duration = 60 * 1000; // by millisecond
        
        Socket socket = new Socket();
        socket.setTcpNoDelay(true);
        socket.setSoTimeout(req_timeout_duration);
        socket.connect(new InetSocketAddress(ipv4, port));

        Instruction ins = Instruction.newBuilder().setCmd(eCommand.SYS_RESET_PASSWORD).setId(0l).build();
        SysPwdReset.Builder req = SysPwdReset.newBuilder().setCmd(ins)
                .setProof(Warrant.newBuilder().setAdminName(adminName).setCachet(adminKey))
                .setUserId(userId).setPlayerId(playerId).setPlayerName(playerName)
                .setNewPwd(newPwd);

        Cocoon cocoon = new Cocoon(ins.getCmd().getNumber(), ins.getId(), req.build().toByteArray());
        MyCodec.encode(cocoon, socket.getOutputStream());

        socket.close();
    }

    public static void main(String[] args) {
//        PropertyConfigurator.configure(Consts.LOG_FILE_PATH);
        if (args.length < 5) {
            System.out.println("usage: ResetPassword admin key [-uid|-uname|-pid|-pname] newpwd ip port");
            return;
        }
        String adminName = args[0];
        String adminKey = args[1];
        int userId = Integer.parseInt(args[3]);
        long playerId = Long.parseLong(args[4]);
        String playerName = args[5];
        String newPwd = args[6];
        String ip = args[7];
        int port = Integer.parseInt(args[8]);

        try {
            reset(adminName, adminKey, userId, playerId, playerName, newPwd, ip, port);
            System.out.println("[" + adminName + "] reset []'s password");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        }

    }

}
