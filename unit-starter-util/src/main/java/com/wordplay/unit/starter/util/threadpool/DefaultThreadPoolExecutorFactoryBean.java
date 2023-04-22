package com.wordplay.unit.starter.util.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuangpf
 * @date 2023-04-22
 */
public class DefaultThreadPoolExecutorFactoryBean extends AbstractThreadPoolExecutorFactoryBean {

	public static final String DEFAULT_THREAD_NAME_PREFIX = "Default-Thread-Pool-";
	private String threadNamePrefix = "Default-Thread-Pool-";
	private static final ThreadPoolConfig DEFAULT_CONFIG = new ThreadPoolConfig();

	public int getCorePoolSize() {
		return DEFAULT_CONFIG.getCorePoolSize();
	}

	public int getMaximumPoolSize() {
		return DEFAULT_CONFIG.getMaxPoolSize();
	}

	public long getKeepAliveTime() {
		return DEFAULT_CONFIG.getKeepAliveTime();
	}

	public TimeUnit getTimeUnit() {
		return TimeUnit.SECONDS;
	}

	public int getQueueCapacity() {
		return DEFAULT_CONFIG.getQueueCapacity();
	}

	public BlockingQueue<Runnable> getWorkQueue(int capacity) {
		return new LinkedBlockingQueue(capacity);
	}

	public ThreadFactory getThreadFactory() {
		return new ThreadFactory() {
			public Thread newThread(Runnable r) {
				return new Thread(r, DefaultThreadPoolExecutorFactoryBean.this.threadNamePrefix + Thread.currentThread().getName());
			}
		};
	}

	public RejectedExecutionHandler getRejectedExecutionHandler() {
		return null;
	}

	public static ThreadPoolExecutor buildDefaultThreadPoolExecutor(String threadNamePrefix) {
		DefaultThreadPoolExecutorFactoryBean factoryBean = new DefaultThreadPoolExecutorFactoryBean();
		factoryBean.setThreadNamePrefix(threadNamePrefix);
		return factoryBean.build();
	}

	public String getThreadNamePrefix() {
		return this.threadNamePrefix;
	}

	public void setThreadNamePrefix(String threadNamePrefix) {
		this.threadNamePrefix = threadNamePrefix;
	}

}
