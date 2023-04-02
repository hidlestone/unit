package com.wordplay.unit.starter.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wordplay.unit.starter.rbac.entity.Permission;
import com.wordplay.unit.starter.rbac.entity.Role;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission> {

	List<Role> selectAllRolePermission();

	List<Permission> getPermissionListByUserId(Long id);

	Page<Permission> list(Page<Permission> page, Permission permission);
}