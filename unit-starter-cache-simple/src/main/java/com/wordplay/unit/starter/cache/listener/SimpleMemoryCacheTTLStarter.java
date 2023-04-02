package com.wordplay.unit.starter.cache.listener;

import com.wordplay.unit.starter.cache.simple.SimpleMemoryCacheImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 监听器，系统启动后执行主动清理策略
 *
 * @author zhuangpf
 */
@Component
public class SimpleMemoryCacheTTLStarter implements ApplicationListener<ApplicationStartedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMemoryCacheTTLStarter.class);

	private static boolean executed = false;

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		if (!executed) {
			executed = true;
			SimpleMemoryCacheImpl.getInstance().startTtlMonitor();
			LOGGER.info("platform SimpleMemoryCacheImpl TTL monitor is started.");
		}
	}
}
