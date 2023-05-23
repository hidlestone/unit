package com.wordplay.unit.starter.core.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author zhuangpf
 * @date 2023-05-20
 */
public class UnitApplicationContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(UnitApplicationContext.class);
	private static ApplicationContext applicationContext;

	public static void setApplicationContext(ApplicationContext applicationContext) {
		if (applicationContext == null) {
			applicationContext = applicationContext;
			LOGGER.info("IeepApplicationContext ready to use: " + applicationContext);
		}
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static Environment getEnvironment() {
		return getBean(Environment.class);
	}

	public static String getApplicationName() {
		Environment env = getEnvironment();
		return env.getProperty("spring.application.name");
	}

	public static <T> T getBean(String name, Class<T> requiredType) {
		return applicationContext.getBean(name, requiredType);
	}

	public static <T> T getBean(Class<T> requiredType) {
		return applicationContext.getBean(requiredType);
	}

	public static <T> Map<String, T> getBeansOfType(Class<T> requiredType) {
		return BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, requiredType);
	}

	public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
		return applicationContext.getBeansWithAnnotation(annotationType);
	}

	public static Object getBean(String name) {
		return applicationContext.getBean(name);
	}

	public static String getProperty(String key) {
		return applicationContext.getEnvironment().getProperty(key);
	}

	public static DataSource getDataSource() {
		return (DataSource) applicationContext.getBean(DataSource.class);
	}

}
