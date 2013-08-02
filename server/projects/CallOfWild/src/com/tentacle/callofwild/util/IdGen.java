package com.tentacle.callofwild.util;

public class IdGen {	
	/*
	 * https://github.com/twitter/snowflake.git
	 * 
	 *  s | timestamp | datacenter | worker | seq |
	 *  1 |    41     |     5      |   5    |  12 |
	 */
	private long workerId = 1L;
	private long datacenterId = 1L;
	private long lastTimestamp = -1L;
	private long sequence = 0L;
	private final long epoch = 1359648000000L;	//2013-1-1 00:00:00
	private final long workerIdBits = 5L;
	private final long datacenterIdBits = 5L;
	private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
	private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
	private final long sequenceBits = 12L;

	private final long workerIdShift = sequenceBits;
	private final long datacenterIdShift = sequenceBits + workerIdBits;
	private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
	private final long sequenceMask = -1L ^ (-1L << sequenceBits);
	
    public IdGen(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("worker id illegal");
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException("datacenter id illegal");
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

        // a little drawback, sometimes for test, need to adjust time backward
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards." +
            		"Refusing to generate id for [" + (lastTimestamp - timestamp) + "] milliseconds");
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
	
}
