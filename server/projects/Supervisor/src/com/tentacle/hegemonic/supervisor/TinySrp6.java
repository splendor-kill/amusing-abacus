package com.tentacle.hegemonic.supervisor;

public class TinySrp6 {
	
	int k = 3;
	int g = 29;
	int N = 31;

	int a = 5;
	int I = 10;

	int b = 6;
	int s = 9;
	
	
	int x,v,A,B,u;
	int usrS,usrK,srvS,srvK;
	
	// return (a^b)%n
	public static int expmod(int a, int b, int n) {
		int x = 1;
		while (b-- > 0) {
			x *= a;
			x %= n;
		}
		return x;
	}
	
	public static int h(int x) {
		final int  factor = 71;
		final int  M = 79;
		return (factor * x) % M;
	}

	public static int h(int x, int y) {
		return h(x + y);
	}
	
	public void getx() {
		x = h(s, I);
		System.out.println("x=" + x);
	}

	public void getv() {
		v = expmod(g, x, N);
		System.out.println("v=" + v);
	}
	
	public void usrS() {
		int t1 = B - k * expmod(g, x, N);
		while (t1 < 0) t1 += N;
		int t2 = a + u * x;
		usrS = expmod(t1, t2, N);
		System.out.println("usrS=" + usrS);
	}
	
	public void srvS() {
		int t = A * expmod(v, u, N);
		srvS = expmod(t, b, N);
		System.out.println("srvS=" + srvS);
	}

	public void getA() {
		A = expmod(g, a, N);
		System.out.println("A=" + A);
	}

	public void getB() {
		B = k * v + expmod(g, b, N);
		B %= N;
		System.out.println("B=" + B);
	}

	public void getu() {
		u = h(A, B);
		System.out.println("u=" + u);
	}

	public void usrK() {
		usrK = h(usrS);
		System.out.println("usrK=" + usrK);
	}

	public void srvK() {
		srvK = h(srvS);
		System.out.println("srvK=" + srvK);
		
	}
	
	public static void main(String[] args) {
		TinySrp6 srp = new TinySrp6();
//		int a = 13;
//		int b = 11;
//		int n = 29;
//		int x = srp.expmod(a, b, n);
//		System.out.println(""+a+"^"+b+"%"+n+"="+x+"");

		srp.getx();
		srp.getv();
		srp.getA();
		srp.getB();
		srp.getu();
		srp.usrS();
		srp.srvS();
		srp.usrK();
		srp.srvK();
		
	}

}
