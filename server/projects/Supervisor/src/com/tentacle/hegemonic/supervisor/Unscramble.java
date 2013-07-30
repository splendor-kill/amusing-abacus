package com.tentacle.hegemonic.supervisor;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

public class Unscramble {
    private static final String CLASS_NAME_PREFIX_PLAYER = "com.tentacle.trickraft.protocol.ProtoPlayer$";
    private static final String CLASS_NAME_PREFIX_ALLIANCE = "com.tentacle.trickraft.protocol.ProtoAlliance$";

    public static void main(String[] args) {
        String file = null;
        String fullClassName = null;
        
        if (args.length == 2) {
            file = args[0];
            fullClassName = CLASS_NAME_PREFIX_PLAYER + args[1];
        } else if (args.length == 3) {
            file = args[1];
            if ("-a".equalsIgnoreCase(args[0])) {
                fullClassName = CLASS_NAME_PREFIX_ALLIANCE + args[2];
            } else if ("-p".equalsIgnoreCase(args[0])) {
                fullClassName = CLASS_NAME_PREFIX_PLAYER + args[2];
            }
        } else {
            System.out.println("usage: unscramble [OPTION] file type\n"
                    + "OPTION:\t-p player\n" + "\t-a alliance\n");
            return;
        }
        
        try {
            Class<?> c = Class.forName(fullClassName);
            System.out.println(c.toString());
            Method parse = c.getMethod("parseFrom", new Class<?>[]{InputStream.class});
            Object o = parse.invoke(null, new FileInputStream(file));
            System.out.println(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        

    }

}
