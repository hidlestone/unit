package com.wordplay.unit.starter.mvc.config;

import com.wordplay.unit.starter.sysparam.util.SysParamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zhuangpf
 * @date 2023-05-18
 */
@Configuration
@Import({FeignClientsConfiguration.class})
@Order(2147483647)
public class WebConfig implements WebMvcConfigurer {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebConfig.class);

	@Autowired
	private static SysParamUtil sysParamUtil;

	
	
	
}
