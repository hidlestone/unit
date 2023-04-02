package com.wordplay.unit.starter.rbac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.rbac.entity.Role;

import java.util.List;

public interface RoleService extends IService<Role> {

	List<Role> getRolesByUserId(Long userId);

	Leaf<Role> page(Role role);

	List<Role> getAllRole();

}
