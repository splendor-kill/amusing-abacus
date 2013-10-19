package com.tentacle.game.server;

import org.jboss.netty.channel.Channel;

import com.tentacle.common.protocol.ProtoPlayer.CheckIdReq;

public class SessionScene {
    private static long cmd_id_counter;
    
    private long cmdId;
    private Channel channel;
    private CheckIdReq req;
    private long punch;
    
    public SessionScene(Channel channel, CheckIdReq req) {
        this.cmdId = cmd_id_counter++;
        this.channel = channel;
        this.req = req;
        this.punch = System.currentTimeMillis();
    }
    
    public long getCmdId() {
        return cmdId;
    }

    public Channel getChannel() {
        return channel;
    }

    public CheckIdReq getReq() {
        return req;
    }
    
    public long getPunch() {
        return punch;
    }

 
}
