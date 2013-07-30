package com.tentacle.callofwild.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class IdGen {


	//gen 1 id per 2^6 millisecond, in other words, gen 2^4 ids per second
	//it will be duplicated after 2^(31+6) ms
	//private volatile long start = (System.currentTimeMillis() >> 6) & 0x7fffffff;// ^ System.nanoTime() ^ System.identityHashCode(this);
	
	private volatile long start = System.currentTimeMillis();
	
	public IdGen(String Lable) {
	}
	
	public synchronized long get() {
		return start++;
	}
	
	/*
	 * https://github.com/twitter/snowflake.git
	 * 
	 *  0 | timestamp | detacenter | worker | seq |
	 *  1 |    41     |     5      |   5    |  12 |
	 */
	private long workerId = 1L;
	private long datacenterId = 1L;
	private long lastTimestamp = -1L;
	private long sequence = 0L;
	private long epoch = 1293811200000L;	//2011-1-1, 00:00:00
	private long workerIdBits = 5L;
	private long datacenterIdBits = 5L;
	private long maxWorkerId = -1L ^ (-1L << workerIdBits);
	private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
	private long sequenceBits = 12L;
	private long workerIdShift = sequenceBits;
	private long datacenterIdShift = sequenceBits + workerIdBits;
	private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
	private long sequenceMask = -1L ^ (-1L << sequenceBits);
	
	public IdGen(long workerId, long datacenterId) {
		if (workerId > maxWorkerId || workerId < 0) {
			// exception;
		}
		if (datacenterId > maxDatacenterId || datacenterId < 0) {
			// exception
		}
		this.workerId = workerId;
		this.datacenterId = datacenterId;
	}
	
	public long getId() {
		return nextId();
	}
	
	private synchronized long nextId() {
		long timestamp = System.currentTimeMillis();

		if (lastTimestamp == timestamp) {
			sequence = (sequence + 1) & sequenceMask;
			if (sequence == 0) {
				timestamp = tilNextMillis(lastTimestamp);
			}
		} else {
			sequence = 0;
		}

		if (timestamp < lastTimestamp) {
			// exception
		}

		lastTimestamp = timestamp;

		return ((timestamp - epoch) << timestampLeftShift)
				| (datacenterId << datacenterIdShift)
				| (workerId << workerIdShift) | sequence;
	}

	private long tilNextMillis(long lastTimestamp) {
		long timestamp = System.currentTimeMillis();
		while (timestamp <= lastTimestamp) {
			timestamp = System.currentTimeMillis();
		}
		return timestamp;
	}
	
	
	
	public static void main(String args[]) {
		Calendar c = Calendar.getInstance();
		
		c.clear();
		c.set(2000, 0, 1);
		long thatPoint = c.getTimeInMillis();
		
		c.clear();
		c.set(2012, 0, 1);
		long anotherPoint = c.getTimeInMillis();
		
		c.clear();
		c.set(2000, 0, 1, 0, 0, 1);
		long p3 = c.getTimeInMillis();
		
		c.clear();
		c.set(2000, 0, 1, 0, 1, 1);
		long p4 = c.getTimeInMillis();
		
		c.clear();
		c.set(2000, 0, 1, 0, 0, 2);
		long p5 = c.getTimeInMillis();

		int sft = 7;
		int msk = 0x7fffffff;
		int mskbits = 31;
		
		long p6 = p3 + (1L << (sft + mskbits)) - (1L << sft) + 1;
		Date d6 = new Date(p6);
		
		c.clear();
		c.set(2000, 0, 1, 0, 1, 3);
		long p7 = c.getTimeInMillis();
		
		c.clear();
		c.set(2011, 0, 1, 0, 0, 0);
		long p8 = c.getTimeInMillis();
		

		System.out.println("p3[" + p3 + ", " + ((p3 >> sft) & msk) + "], p4["
				+ p4 + ", " + ((p4 >> sft) & msk) + "], p5[" + p5 + ", "
				+ ((p5 >> sft) & msk) + "], p6[" + p6 + ", "
				+ ((p6 >> sft) & msk) + "], d6[" + d6 + "], p7[" + p7 + ", "
				+ ((p5 >> sft) & msk) + "]");

		long cur = System.currentTimeMillis();
		System.out.println("cur[" + cur + "], p8[" + p8 + "], diff["
				+ (cur - p8) + "], pre["+(new Date(cur-0xffffffffL))+"]");		
		

		long p = p3 + 1000 * 60;
		/*while (true) {
			p++;
			if (((p >> 16) & 0x0fffffff) == ((p3 >> 16) & 0x0fffffff)) {
				Date dt = new Date(p);
				System.out.println(dt);
				break;
			}
		}
		*/
		
		int s = 10;
		int e = 16;
		while (true) {
			e++;
			if (((s >> 3) & 0x0ff) == ((e >> 3) & 0x0ff)) {
				System.out.println(e);
				System.out.println((e == 2056) ? "yes" : "no");
				break;
			}
		}
		

		/*
		System.out.println("thatPoint[" + thatPoint + "], anotherPoint["
				+ anotherPoint + "], p3[" + p3 + "], cur[" + cur + "], offset["
				+ (cur - thatPoint) + "]");
		System.out.println("shorten that[" + (thatPoint & 0x0fffffff)
				+ "], shorten p3[" + (p3 & 0x0fffffff) + "]");
		System.out.println("nano1["+System.nanoTime()+"], nano2["+System.nanoTime()+"]");
		*/

		Random rnd = new Random();
		for (int i = 0; i < 10; i++) {
			new IdGen("");
//			try {
//				Thread.sleep(rnd.nextInt(10));
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		
		IdGen tw = new IdGen(1, 1);
		for (int i = 0; i < 100; i++)
			System.out.println(tw.getId()&0xfff);
		
	}
}
