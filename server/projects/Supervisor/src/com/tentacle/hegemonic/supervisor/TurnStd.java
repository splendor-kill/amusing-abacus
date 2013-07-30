package com.tentacle.hegemonic.supervisor;

import java.io.OutputStream;
import java.io.PrintStream;

public class TurnStd {
    private static final TurnStd inst = new TurnStd();

    private PrintStream printStreamOriginal = System.out;
    private PrintStream nothingStream;

    public static TurnStd out() {
        return inst;
    }

    private TurnStd() {
        nothingStream = new PrintStream(new OutputStream() {
            public void write(int b) {
            }
        });
    }

    public void on() {
        System.setOut(printStreamOriginal);
    }

    public void off() {
        System.setOut(nothingStream);
    }

    public static void main(String[] args) {
        System.out.println("hello");
        TurnStd.out().off();
        System.out.println("fuck");
        TurnStd.out().on();
        System.out.println("world");
    }

}
