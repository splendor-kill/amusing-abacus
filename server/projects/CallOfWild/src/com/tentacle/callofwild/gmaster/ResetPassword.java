package com.tentacle.callofwild.gmaster;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.tentacle.callofwild.protocol.MyCodec;
import com.tentacle.callofwild.protocol.MyCodec.Cocoon;
import com.tentacle.callofwild.protocol.ProtoAdmin.SysPwdReset;
import com.tentacle.callofwild.protocol.ProtoAdmin.Warrant;
import com.tentacle.callofwild.protocol.ProtoBasis.Instruction;
import com.tentacle.callofwild.protocol.ProtoBasis.eCommand;
import com.tentacle.callofwild.util.Consts;
import com.tentacle.callofwild.util.Utils;

public class ResetPassword {
    private static final Logger logger = Logger.getLogger(ResetPassword.class);
    
    public static void reset(String adminName, String adminKey, 
            int userId, long playerId, String playerName, String newPwd) throws IOException {
        final int req_timeout_duration = 60 * 1000; // by millisecond
        
        String ipv4 = Utils.getConfig().getProperty("game_server.ipv4", "127.0.0.1");
        String tmp = Utils.getConfig().getProperty("game_server.listening_port", "10000");
        int port = Integer.parseInt(tmp);

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
        PropertyConfigurator.configure(Consts.LOG_FILE_PATH);
        if (args.length < 5) {
            System.out.println("usage: ResetPassword admin key [-uid|-uname|-pid|-pname] newpwd");
            return;
        }
        String adminName = args[0];
        String adminKey = args[1];
        int userId = Integer.parseInt(args[3]);
        long playerId = Long.parseLong(args[4]);
        String playerName = args[5];
        String newPwd = args[6];

        try {
            reset(adminName, adminKey, userId, playerId, playerName, newPwd);
            System.out.println("[" + adminName + "] reset []'s password");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        }

    }

}
