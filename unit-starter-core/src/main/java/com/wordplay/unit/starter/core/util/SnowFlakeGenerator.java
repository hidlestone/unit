package com.wordplay.unit.starter.core.util;

/**
 * @author zhuangpf
 * @date 2023-05-22
 */
public class SnowFlakeGenerator {

	private static final long START_STMP = 1553669901331L;
	private static final long SEQUENCE_BIT = 12L;
	private static final long APPLICATION_BIT = 8L;
	private static final long MAX_APPLICATION_NUM = 255L;
	private static final long MAX_SEQUENCE = 4095L;
	private static final long APPLICATION_LEFT = 12L;
	private static final long TIMESTMP_LEFT = 24L;
	private long applicationId;
	private long sequence = 0L;
	private long lastStmp = -1L;

	public SnowFlakeGenerator(long applicationId) {
		if (applicationId <= 255L && applicationId >= 0L) {
			this.applicationId = applicationId;
		} else {
			throw new IllegalArgumentException("ApplicationId can't be greater than 255 or less than 0");
		}
	}

	public synchronized long nextId() {
		long currStmp = this.getNewstmp();
		if (currStmp < this.lastStmp) {
			this.lastStmp = currStmp;
		}

		if (currStmp == this.lastStmp) {
			this.sequence = this.sequence + 1L & 4095L;
			if (this.sequence == 0L) {
				currStmp = this.getNextMill();
			}
		} else {
			this.sequence = 0L;
		}

		this.lastStmp = currStmp;
		return currStmp - 1553669901331L << 24 | this.applicationId << 12 | this.sequence;
	}

	private long getNextMill() {
		long mill;
		for (mill = this.getNewstmp(); mill <= this.lastStmp; mill = this.getNewstmp()) {
			;
		}
		return mill;
	}

	private long getNewstmp() {
		return System.currentTimeMillis();
	}

	public static void main(String[] args) {
		SnowFlakeGenerator snowFlake = new SnowFlakeGenerator(127L);
		long start = System.currentTimeMillis();
		System.out.println(start);

		for (int i = 0; i < 1; ++i) {
			System.out.println(snowFlake.nextId());
		}

		System.out.println(System.currentTimeMillis() - start);
	}
	
}
