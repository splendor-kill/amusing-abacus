package com.tentacle.gmaster;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.tentacle.common.protocol.MyCodec;
import com.tentacle.common.protocol.MyCodec.Cocoon;
import com.tentacle.common.protocol.ProtoAdmin.SysReloadCfg;
import com.tentacle.common.protocol.ProtoAdmin.Warrant;
import com.tentacle.common.protocol.ProtoBasis.Instruction;
import com.tentacle.common.protocol.ProtoBasis.eCommand;


public class ReloadConfig {
    private static final Logger logger = Logger.getLogger(ReloadConfig.class);
    
    public static void reloadCfg(String adminName, String adminKey, String whichCfg, int port) throws IOException {        
        final int req_timeout_duration = 60 * 1000; // by millisecond

        Socket socket = new Socket();
        socket.setTcpNoDelay(true);
        socket.setSoTimeout(req_timeout_duration);
        socket.connect(new InetSocketAddress("localhost", port));
        
        Instruction ins = Instruction.newBuilder().setCmd(eCommand.SYS_RELOAD_CFG).setId(0l).build();
        SysReloadCfg.Builder req = SysReloadCfg.newBuilder().setCmd(ins)
                .setProof(Warrant.newBuilder().setAdminName(adminName).setCachet(adminKey))
                .setWhichCfg(whichCfg);
        
        Cocoon cocoon = new Cocoon(ins.getCmd().getNumber(), ins.getId(), req.build().toByteArray());       
        MyCodec.encode(cocoon, socket.getOutputStream());       
    }

    public static void main(String[] args) {
//        PropertyConfigurator.configure(Consts.LOG_FILE_PATH);
        if (args.length < 4) {
            System.out.println("usage: ReloadConfig name key whichcfg port");
            return;
        }
        String adminName = args[0];
        String adminKey = args[1];
        String whichCfg = args[2];
        
        try {
            int port = Integer.parseInt(args[3]);
            reloadCfg(adminName, adminKey, whichCfg, port);
            String fingerprint = "[" + adminName + "] reload the config[" + whichCfg + "].";
            System.out.println(fingerprint);
            logger.info(fingerprint);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }

    }

}
