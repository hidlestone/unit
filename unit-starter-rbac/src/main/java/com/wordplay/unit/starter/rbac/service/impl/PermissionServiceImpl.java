package com.wordplay.unit.starter.rbac.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.api.response.ResponseResult;
import com.wordplay.unit.starter.cache.redis.util.RedisUtil;
import com.wordplay.unit.starter.data.mp.util.LeafPageUtil;
import com.wordplay.unit.starter.rbac.constant.RbacStarterConstant;
import com.wordplay.unit.starter.rbac.entity.Permission;
import com.wordplay.unit.starter.rbac.entity.Role;
import com.wordplay.unit.starter.rbac.entity.UserRole;
import com.wordplay.unit.starter.rbac.mapper.PermissionMapper;
import com.wordplay.unit.starter.rbac.mapper.UserRoleMapper;
import com.wordplay.unit.starter.rbac.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PermissionServiceImpl.class);

	@Autowired
	private UserRoleMapper userRoleMapper;
	@Autowired
	private RedisUtil redisUtil;

	@Override
	public ResponseResult refreshPermissionCache() {
		// 获取所有角色权限
		List<Role> rolePermissionList = this.baseMapper.selectAllRolePermission();
		if (CollectionUtil.isEmpty(rolePermissionList)) {
			return ResponseResult.success();
		}
		// 按照角色分组
		Map<Long, List<Role>> rolePermMap =
				rolePermissionList.stream().collect(Collectors.groupingBy(Role::getId));
		for (Map.Entry<Long, List<Role>> entry : rolePermMap.entrySet()) {
			redisUtil.hset(RbacStarterConstant.CACHE_KEY_ROLE_PERMISSION, String.valueOf(entry.getKey()), entry.getValue());
		}
		LOGGER.info("role permission cache has refreshed.");
		return ResponseResult.success();
	}

	@Override
	public List<Permission> getPermissionListByUserId(Long id) {
		// 1、从缓存中获取
		// 当前用户拥有的角色
		Wrapper<UserRole> wrapper = new QueryWrapper<>();
		List<UserRole> userRoles = userRoleMapper.selectList(wrapper);
		// 所有的权限
		List<Permission> allRolePermList = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(userRoles)) {
			for (UserRole item : userRoles) {
				allRolePermList.addAll((List<Permission>) redisUtil.hget(RbacStarterConstant.CACHE_KEY_ROLE_PERMISSION, String.valueOf(item.getRoleId())));
			}
		}
		if (CollectionUtil.isNotEmpty(allRolePermList)) {
			// 权限去重
			allRolePermList = allRolePermList.stream().collect(Collectors.collectingAndThen(
					Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Permission::getPermissionCode))), ArrayList::new));
			return allRolePermList;
		}
		// 2、从数据库获取
		allRolePermList = this.baseMapper.getPermissionListByUserId(id);
		return allRolePermList;
	}

	@Override
	public Leaf<Permission> page(Permission permission) {
		Page<Permission> page = new Page<>(permission.getPageNum(), permission.getPageSize());
		page = this.baseMapper.list(page, permission);
		return LeafPageUtil.pageToLeaf(page);
	}
	
}
