package com.wordplay.unit.starter.util.threadpool;

import java.io.Serializable;

/**
 * @author zhuangpf
 * @date 2023-04-22
 */
public class ThreadPoolConfig implements Serializable {

	private static final long serialVersionUID = -1515308989167724953L;

	// 核心线程数量
	private int corePoolSize = 50;
	// 最大线程数量
	private int maxPoolSize = 100;
	// 工作队列
	private int queueCapacity = 3000;
	// 空闲线程存活时间。单位：秒。
	private long keepAliveTime = 30L;
	// 线程名称前缀
	private String threadNamePrefix = "";

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public int getQueueCapacity() {
		return queueCapacity;
	}

	public void setQueueCapacity(int queueCapacity) {
		this.queueCapacity = queueCapacity;
	}

	public long getKeepAliveTime() {
		return keepAliveTime;
	}

	public void setKeepAliveTime(long keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

	public String getThreadNamePrefix() {
		return threadNamePrefix;
	}

	public void setThreadNamePrefix(String threadNamePrefix) {
		this.threadNamePrefix = threadNamePrefix;
	}

}
