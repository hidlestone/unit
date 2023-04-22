package com.wordplay.unit.starter.util.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 抽象线程工厂
 *
 * @author zhuangpf
 * @date 2023-04-22
 */
public abstract class AbstractThreadPoolExecutorFactoryBean {

	public abstract int getCorePoolSize();

	public abstract int getMaximumPoolSize();

	public abstract long getKeepAliveTime();

	public abstract TimeUnit getTimeUnit();

	public abstract int getQueueCapacity();

	public abstract BlockingQueue<Runnable> getWorkQueue(int var1);

	public abstract ThreadFactory getThreadFactory();

	public abstract RejectedExecutionHandler getRejectedExecutionHandler();

	public ThreadPoolExecutor build() {
		ThreadFactory threadFactory = this.getThreadFactory();
		RejectedExecutionHandler rejectedExecutionHandler = this.getRejectedExecutionHandler();
		if (threadFactory != null && rejectedExecutionHandler != null) {
			return new ThreadPoolExecutor(this.getCorePoolSize(), this.getMaximumPoolSize(), this.getKeepAliveTime(), this.getTimeUnit(), this.getWorkQueue(this.getQueueCapacity()), threadFactory, this.getRejectedExecutionHandler());
		} else if (threadFactory != null) {
			return new ThreadPoolExecutor(this.getCorePoolSize(), this.getMaximumPoolSize(), this.getKeepAliveTime(), this.getTimeUnit(), this.getWorkQueue(this.getQueueCapacity()), threadFactory);
		} else {
			return rejectedExecutionHandler != null ? new ThreadPoolExecutor(this.getCorePoolSize(), this.getMaximumPoolSize(), this.getKeepAliveTime(), this.getTimeUnit(), this.getWorkQueue(this.getQueueCapacity()), rejectedExecutionHandler) : new ThreadPoolExecutor(this.getCorePoolSize(), this.getMaximumPoolSize(), this.getKeepAliveTime(), this.getTimeUnit(), this.getWorkQueue(this.getQueueCapacity()));
		}
	}
	
}
