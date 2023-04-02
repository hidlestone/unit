package com.wordplay.unit.starter.i18n.listener;

import com.wordplay.unit.starter.api.response.ResponseResult;
import com.wordplay.unit.starter.i18n.service.I18nResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

/**
 * 多语言词条初始化监听器
 *
 * @author zhuangpf
 */
//@Component 暂时注释
public class I18nResourceInitListener implements ApplicationListener<ApplicationStartedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(I18nResourceInitListener.class);

	@Autowired
	private I18nResourceService i18nResourceService;

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		// 启动时更新多语言词条
		ResponseResult responseResult = i18nResourceService.refreshI18nResourceCache();
		LOGGER.info(responseResult.toString());
		LOGGER.info("init i18n resources to cache.");
	}
	
}
