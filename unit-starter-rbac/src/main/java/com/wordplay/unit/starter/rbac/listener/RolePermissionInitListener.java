package com.wordplay.unit.starter.rbac.listener;

import com.wordplay.unit.starter.api.response.ResponseResult;
import com.wordplay.unit.starter.rbac.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 启动时初始化系统角色权限到缓存中
 *
 * @author zhuangpf
 */
@Component
public class RolePermissionInitListener implements ApplicationListener<ApplicationStartedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RolePermissionInitListener.class);

	@Autowired
	private PermissionService permissionService;

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		ResponseResult responseResult = permissionService.refreshPermissionCache();
		LOGGER.info("init role permission to cache.");
	}
}
