package com.tentacle.callofwild.antiseptic;

public class MyFault extends Exception {
	private static final long serialVersionUID = 3483508501385571280L;

	public MyFault(String msg) {
		super(msg);
	}
}
