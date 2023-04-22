package com.wordplay.unit.starter.util.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author zhuangpf
 * @date 2023-04-22
 */
public class ThreadPoolExecutorFactoryBean extends AbstractThreadPoolExecutorFactoryBean {

	private String threadPoolName;

	private ThreadPoolExecutorFactoryBean(String threadPoolName) {
		this.threadPoolName = threadPoolName;
	}

	public static synchronized ThreadPoolExecutorFactoryBean getBean(String threadPoolName) {
		return new ThreadPoolExecutorFactoryBean(threadPoolName);
	}

	public int getCorePoolSize() {
		return this.getConfig(this.threadPoolName).getCorePoolSize();
	}

	public int getMaximumPoolSize() {
		return this.getConfig(this.threadPoolName).getMaxPoolSize();
	}

	public int getQueueCapacity() {
		return this.getConfig(this.threadPoolName).getQueueCapacity();
	}

	public long getKeepAliveTime() {
		return this.getConfig(this.threadPoolName).getKeepAliveTime();
	}

	public TimeUnit getTimeUnit() {
		return TimeUnit.SECONDS;
	}

	public BlockingQueue<Runnable> getWorkQueue(int capacity) {
		return new LinkedBlockingQueue(capacity);
	}

	public ThreadFactory getThreadFactory() {
		return new ThreadFactory() {
			public Thread newThread(Runnable r) {
				return new Thread(r, ThreadPoolExecutorFactoryBean.this.getConfig(ThreadPoolExecutorFactoryBean.this.threadPoolName).getThreadNamePrefix() + Thread.currentThread().getName());
			}
		};
	}

	public RejectedExecutionHandler getRejectedExecutionHandler() {
		return null;
	}

	private ThreadPoolConfig getConfig(String threadPoolName) {
		ThreadPoolConfig threadPoolConfig = new ThreadPoolConfig();
		return threadPoolConfig;
	}

	public String getThreadPoolName() {
		return this.threadPoolName;
	}

	public void setThreadPoolName(String threadPoolName) {
		this.threadPoolName = threadPoolName;
	}

}
