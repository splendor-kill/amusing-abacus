package com.tentacle.callofwild.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class MyCodec {
    
    public static class Cocoon {
        public Cocoon(int cmdType, long cmdId, byte[] dat) {
            this.cmdType = cmdType;
            this.cmdId = cmdId;
            this.dat = dat;
        }

        public int cmdType;
        public long cmdId;
        public byte[] dat;
    }	
	
	private static final int msg_prepender_total_length 	= 16;
	
    /* package header:
     * +-----------------|-----------------|---------------+
     * | body_len(int32) | cmd_type(int32) | cmd_id(int64) |
     * +-----------------|-----------------|---------------+
     */
	
	public static void encode(Cocoon msg, OutputStream os) throws IOException {		
		DataOutputStream dos = new DataOutputStream(os);
		dos.writeInt(msg.dat.length);
		dos.writeInt(msg.cmdType);
		dos.writeLong(msg.cmdId);
		dos.write(msg.dat);
		dos.flush();
	}	
	
    public static Cocoon decode(InputStream in) throws IOException {
    	if (in == null)
    		return null;
    	DataInputStream dis = new DataInputStream(in);    	
    	if (dis.available() < msg_prepender_total_length) {
    		return null;
    	}
    	dis.mark(1024);

    	int bodyLen = dis.readInt();
    	int cmdType = dis.readInt();
		long cmdId = dis.readLong();
		
		if (bodyLen < 0 || dis.available() < 0) {
//		    System.out.println("body len:" + bodyLen + " cmd type:" + cmdType + " cmd id:" + cmdId + " readable:" + dis.available());
			return null;
		}
		
    	if (dis.available() < bodyLen) {
    		dis.reset();
    		return null;
    	}
    	byte[] decoded = new byte[bodyLen];
    	dis.read(decoded);    	

        return new Cocoon(cmdType, cmdId, decoded);    	
    }
    
    /* package header:
     * +-----------------|-----------------|---------------+
     * | body_len(int32) | cmd_type(int32) | cmd_id(int64) |
     * +-----------------|-----------------|---------------+
     */

	public static class MyEncoder extends OneToOneEncoder {
		@Override
		protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
			if (!(msg instanceof Cocoon)) return msg;
			
			Cocoon v = (Cocoon) msg;
			ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
			buf.writeInt(v.dat.length);
			buf.writeInt(v.cmdType);
			buf.writeLong(v.cmdId);
			buf.writeBytes(v.dat);
			return buf;
		}
	}

	public static class MyDecoder extends FrameDecoder {
		@Override
		protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) {			
			if (buffer.readableBytes() < msg_prepender_total_length) {
				return null;
			}
			buffer.markReaderIndex();
			
			int bodyLen = buffer.readInt();
			int cmdType = buffer.readInt();
			long cmdId = buffer.readLong();

			if (bodyLen < 0 || buffer.readableBytes() < 0) {
//			    System.out.println("body len:" + bodyLen + " cmd type:" + cmdType + " cmd id:" + cmdId + " readable:" + buffer.readableBytes());
				return null;
			}
			if (buffer.readableBytes() < bodyLen) {
				buffer.resetReaderIndex();
				return null;
			}
						
			byte[] decoded = new byte[bodyLen];
			buffer.readBytes(decoded);

			return new Cocoon(cmdType, cmdId, decoded);
		}
	}

}
