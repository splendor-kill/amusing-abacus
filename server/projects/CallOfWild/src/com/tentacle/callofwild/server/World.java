package com.tentacle.callofwild.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import com.google.protobuf.Message;
import com.tentacle.callofwild.protocol.MyCodec.Cocoon;
import com.tentacle.callofwild.protocol.ProtoBasis.Instruction;
import com.tentacle.callofwild.protocol.ProtoBasis.eCommand;
import com.tentacle.callofwild.protocol.ProtoBasis.eErrorCode;






public class World {
    private static final Logger logger = Logger.getLogger(World.class);
    private static World world = new World();
    
    public GameServer server = null;
    
    private TimerManager timerManager = TimerManager.getInstance(); 
    // channel --> player id
    private ConcurrentMap<Channel, Long> playerChannel = new ConcurrentHashMap<Channel, Long>();
    // event_type -> the instances response that event
    public Map<Integer, List<Object>> subscribers = new HashMap<Integer, List<Object>>();

    public static World getInstance() {
        return world;
    }
    
    private World() {
    }
    
    public void progress(long frameTime) {  
        timerManager.elapse(frameTime);
    }
    
    public void setupTimer() {
        
    }
    
    public int getPlayerChannelSize() {
        return playerChannel.size();
    }
    
    public Channel getChannel(long playerId) {
        for (Entry<Channel, Long> i : playerChannel.entrySet()) {
            if (i.getValue() == playerId)
                return i.getKey();
        }
        return null;        
    }
    
    public void removeChannel(Channel ch) {
        Long playerId = playerChannel.remove(ch);
        if (playerId != null) {
            logger.debug("remove player[" + playerId + "]'s channel" + ch
                    + ", current onliners: " + getPlayerChannelSize());
        }
        ch.close();    
    }

    public void init() {
        
    }
    
    public boolean load() {
        return false;
    }
    
    public void send(Channel ch, Instruction ins, eErrorCode ec, Message dat) {
        if (ch == null) return;
        eCommand cmd = ins.getCmd();
        long cmdId = ins.getId();
        Cocoon wrapper = new Cocoon(cmd.getNumber(), cmdId, (dat != null) ? dat.toByteArray() : new byte[0]);
        ch.write(wrapper);
        wrapper.dat = null;
        wrapper = null;
        
        logger.debug("[" + cmd + "]: send[" + ec + "] to [" + ch.getRemoteAddress() + "]");     
    }
    
}

