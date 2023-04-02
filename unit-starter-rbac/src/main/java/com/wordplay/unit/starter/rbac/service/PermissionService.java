package com.wordplay.unit.starter.rbac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.api.response.ResponseResult;
import com.wordplay.unit.starter.rbac.entity.Permission;

import java.util.List;

public interface PermissionService extends IService<Permission> {

	/**
	 * 更新权限信息到缓存中
	 *
	 * @return 是否更新成功
	 */
	ResponseResult refreshPermissionCache();

	/**
	 * @param id 用户ID
	 * @return 资源权限列表
	 */
	List<Permission> getPermissionListByUserId(Long id);

	/**
	 * @param permission 请求参数
	 * @return 分页的权限列表
	 */
	Leaf<Permission> page(Permission permission);
}
