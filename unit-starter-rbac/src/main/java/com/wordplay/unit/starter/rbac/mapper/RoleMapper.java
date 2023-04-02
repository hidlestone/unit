package com.wordplay.unit.starter.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wordplay.unit.starter.rbac.entity.Role;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {

	List<Role> getRolesByUserId(Long userId);

	Page<Role> page(Page<Role> page, Role role);
}