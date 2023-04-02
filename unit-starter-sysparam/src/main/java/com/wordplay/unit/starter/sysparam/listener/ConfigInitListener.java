package com.wordplay.unit.starter.sysparam.listener;

import com.wordplay.unit.starter.api.response.ResponseResult;
import com.wordplay.unit.starter.sysparam.service.impl.PlatformSysParamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

/**
 * 系统参数初始化监听器
 *
 * @author zhuangpf
 */
//@Component // 暂时注释
public class ConfigInitListener implements ApplicationListener<ApplicationStartedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigInitListener.class);

	@Autowired
	private PlatformSysParamUtil platformSysParamUtil;

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		ResponseResult responseResult = platformSysParamUtil.refreshSysParamCache();
		LOGGER.info(responseResult.toString());
		LOGGER.info("init system config to cache.");
	}

}
