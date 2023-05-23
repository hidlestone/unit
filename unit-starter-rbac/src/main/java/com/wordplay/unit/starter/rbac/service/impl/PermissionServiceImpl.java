package com.wordplay.unit.starter.rbac.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.api.response.ResponseResult;
import com.wordplay.unit.starter.cache.redis.util.RedisUtil;
import com.wordplay.unit.starter.data.mp.util.LeafPageUtil;
import com.wordplay.unit.starter.rbac.constant.RbacStarterConstant;
import com.wordplay.unit.starter.rbac.entity.Permission;
import com.wordplay.unit.starter.rbac.entity.Role;
import com.wordplay.unit.starter.rbac.entity.RolePermission;
import com.wordplay.unit.starter.rbac.mapper.PermissionMapper;
import com.wordplay.unit.starter.rbac.model.AuthcTypeEnum;
import com.wordplay.unit.starter.rbac.service.PermissionService;
import com.wordplay.unit.starter.rbac.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PermissionServiceImpl.class);

	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private RoleService roleService;

	/**
	 * 刷新权限缓存
	 */
	@Override
	public ResponseResult refreshPermissionCache() {
		// >>> 删除缓存权限
		List<Role> roleList = roleService.list(Wrappers.lambdaQuery(Role.class).select(Role::getRoleCode));
		if (CollectionUtil.isNotEmpty(roleList)) {
			for (Role role : roleList) {
				redisUtil.del(RbacStarterConstant.CACHE_KEY_PERMISSION_ROLE + role.getRoleCode());
			}
		}
		redisUtil.del(RbacStarterConstant.CACHE_KEY_PERMISSION_AUTH);
		redisUtil.del(RbacStarterConstant.CACHE_KEY_PERMISSION_ANON);
		// 1、更新所有角色权限。permission:role:{roleCode}，角色下的权限
		List<RolePermission> rolePermissionList = this.baseMapper.getAllRolePermission();
		// 按照角色分组
		Map<String, List<String>> rolePermMap = new HashMap<>();
		if (CollectionUtil.isNotEmpty(rolePermissionList)) {
			for (RolePermission rolePermission : rolePermissionList) {
				List<String> permList = rolePermMap.get(rolePermission.getRoleCode());
				if (CollectionUtil.isNotEmpty(permList)) {
					permList.add(rolePermission.getResourceValue());
				} else {
					permList = new ArrayList<>();
					permList.add(rolePermission.getResourceValue());
					rolePermMap.put(rolePermission.getRoleCode(), permList);
				}
			}
		}
		if (CollectionUtil.isNotEmpty(rolePermMap)) {
			for (Map.Entry<String, List<String>> entry : rolePermMap.entrySet()) {
				redisUtil.sSet(RbacStarterConstant.CACHE_KEY_PERMISSION_ROLE + entry.getKey(), entry.getValue());
			}
		}
		// 2、登录授权访问权限：permission:auth，登录访问
		List<Permission> authPermissionList = this.list(Wrappers.lambdaQuery(Permission.class).eq(Permission::getAuthcType, AuthcTypeEnum.AUTH));
		if (CollectionUtil.isNotEmpty(authPermissionList)) {
			List<String> permList = authPermissionList.stream().map(e -> e.getResourceValue()).collect(Collectors.toList());
			redisUtil.sSet(RbacStarterConstant.CACHE_KEY_PERMISSION_AUTH, permList);
		}
		// 3、匿名访问权限：permission:anon，匿名访问
		List<Permission> anonPermissionList = this.list(Wrappers.lambdaQuery(Permission.class).eq(Permission::getAuthcType, AuthcTypeEnum.ANON));
		if (CollectionUtil.isNotEmpty(authPermissionList)) {
			List<String> permList = anonPermissionList.stream().map(e -> e.getResourceValue()).collect(Collectors.toList());
			redisUtil.sSet(RbacStarterConstant.CACHE_KEY_PERMISSION_ANON, permList);
		}
		LOGGER.info("role permission cache has refreshed.");
		return ResponseResult.success();
	}

	@Override
	public List<Permission> getPermissionListByUserId(Long id) {
		List<Permission> allRolePermList = this.baseMapper.getPermissionListByUserId(id);
		return allRolePermList;
	}

	@Override
	public Leaf<Permission> page(Permission permission) {
		Page<Permission> page = new Page<>(permission.getPageNum(), permission.getPageSize());
		page = this.baseMapper.page(page, permission);
		return LeafPageUtil.pageToLeaf(page);
	}

}
