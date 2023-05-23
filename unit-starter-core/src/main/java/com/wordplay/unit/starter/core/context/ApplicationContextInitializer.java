package com.wordplay.unit.starter.core.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author zhuangpf
 * @date 2023-05-20
 */
@Component
@Order(-2147483648) // 默认是最低优先级,值越小优先级越高
public class ApplicationContextInitializer implements ServletContextListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContextInitializer.class);

	public void contextInitialized(ServletContextEvent sce) {
		if (sce.getServletContext().getAttribute("org.springframework.web.context.WebApplicationContext.ROOT") != null) {
			ApplicationContext ctx = (ApplicationContext) sce.getServletContext().getAttribute("org.springframework.web.context.WebApplicationContext.ROOT");
			UnitApplicationContext.setApplicationContext(ctx);
			LOGGER.info("ContextInitialized: " + ctx);
		}

	}

	public void contextDestroyed(ServletContextEvent sce) {
		LOGGER.info("ApplicationContextInitializer contextDestroyed");
	}

}
