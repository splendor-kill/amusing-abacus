package com.tentacle.hegemonic.supervisor;




public class ShutdownHook implements Runnable {
//	public Object lock = new Object();

	public ShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(this));
		System.out.println(">>> shutdown hook registered ");
	}

	@Override
	public void run() {
		System.out.println("\n>>> About to execute: " + ShutdownHook.class.getName() + ".run() to clean up before JVM exits.");
		cleanUp();
		System.out.println(">>> Finished execution: " + ShutdownHook.class.getName() + ".run()");
//		lock.notify();
	}

	private void cleanUp() {
		for (int i = 0; i < 7; i++)
			System.out.println(i);
	}

	public static void main(String[] args) {
//		Signal.handle(new Signal("INT"), new SignalHandler() {
//			public void handle(Signal sig) {
//				System.out.println(sig);
//			}
//		});
//		Signal.handle(new Signal("TERM"), new SignalHandler() {
//			public void handle(Signal sig) {
//				System.out.println(sig);
//			}
//		});
//		Signal.handle(new Signal("ABRT"), new SignalHandler() {
//			public void handle(Signal sig) {
//				System.out.println(sig);
//			}
//		});
		
//		ShutdownHook hook = new ShutdownHook();
//		System.out.println(">>> Sleeping for five seconds, try ctrl-C now if you like. ");
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
		while (true);

//		System.out.println(">>> Slept for 10 seconds and the main thread exited. ");
	}

}
