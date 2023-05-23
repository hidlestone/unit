package com.wordplay.unit.starter.mvc.util;

import com.wordplay.unit.starter.cache.redis.util.RedisUtil;
import com.wordplay.unit.starter.core.context.UnitApplicationContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 应用实例管理
 *
 * @author zhuangpf
 * @date 2023-05-21
 */
public class ApplicationInstanceManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationInstanceManager.class);

	private static String serviceName;
	private static String applicationInstanceName;
	private static Long applicationInstanceId;
	public static final Long MAX_APPLICATION_ID = 255L;

	/**
	 * 获取应用实例名称
	 */
	public static String getApplicationInstanceName() {
		if (applicationInstanceName == null) {
			try {
				Registration registration = UnitApplicationContext.getBean(Registration.class);
				if (registration != null) {
					InetAddress addr = InetAddress.getLocalHost();
					String hostName = addr.getHostName().toString();
					applicationInstanceName = registration.getServiceId() + "-" + hostName;
				}
			} catch (Exception e) {
				applicationInstanceName = getApplicationName() + "-" + System.getenv("COMPUTERNAME");
			}
		}
		return applicationInstanceName;
	}

	/**
	 * 获取服务名称
	 */
	public static String getServiceName() {
		if (serviceName == null) {
			try {
				Registration registration = null;
				registration = UnitApplicationContext.getBean(Registration.class);
				if (registration != null) {
					serviceName = registration.getServiceId();
				}
			} catch (Exception e) {
				serviceName = getApplicationName();
			}
		}
		return serviceName;
	}

	/**
	 * 根据配置文件获取应用名称
	 */
	public static String getApplicationName() {
		YamlPropertiesFactoryBean yamlMapFactoryBean = new YamlPropertiesFactoryBean();
		yamlMapFactoryBean.setResources(new Resource[]{new ClassPathResource("application.yml")});
		Properties properties = yamlMapFactoryBean.getObject();
		String applicationName = properties.getProperty("spring.application.name");
		String version = properties.getProperty("unit.version");
		if (StringUtils.isNotBlank(version)) {
			applicationName = applicationName + version;
		}
		return applicationName;
	}

	/**
	 * 获取实例ID
	 */
	public static Long getApplicationInstanceId() {
		if (applicationInstanceId == null) {
			String applicationName = getApplicationInstanceName();

			try {
				RedisUtil redisUtil = null;
				try {
					redisUtil = UnitApplicationContext.getBean("RedisUtil", RedisUtil.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Map<String, String> applicationIds = null;
				Object cacheObj = redisUtil.get("APPLICATION_IDS");
				if (cacheObj == null) {
					applicationIds = new HashMap();
				} else {
					applicationIds = (Map) redisUtil.get("APPLICATION_IDS");
				}
				applicationInstanceId = ((Map) applicationIds).get(applicationName) != null ? Long.valueOf((String) ((Map) applicationIds).get(applicationName)) : null;
				if (applicationInstanceId == null) {
					if (!((Map) applicationIds).isEmpty()) {
						Collection<String> ids = ((Map) applicationIds).values();
						List<Long> longIds = new ArrayList();
						Iterator var6 = ids.iterator();

						while (var6.hasNext()) {
							String id = (String) var6.next();
							longIds.add(Long.valueOf(id));
						}
						applicationInstanceId = Collections.max(longIds) + 1L;
						if (applicationInstanceId > MAX_APPLICATION_ID) {
							redisUtil.del(new String[]{"APPLICATION_IDS"});
							applicationIds = new HashMap();
							applicationInstanceId = 0L;
						}
					} else {
						applicationInstanceId = 0L;
					}
					applicationIds.put(applicationName, applicationInstanceId.toString());
				}
				redisUtil.set("APPLICATION_IDS", applicationIds);
			} catch (Exception e) {
				LOGGER.warn("Exception occurred when get application id from cache: " + e.getMessage() + ", using hashcode of application id for SnowFlakeIdGenerator instead.");
				applicationInstanceId = Math.abs(applicationName.hashCode()) % 256L;
			}
		}
		return applicationInstanceId;
	}

}
