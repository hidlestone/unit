package com.wordplay.unit.starter.rbac.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.data.mp.util.LeafPageUtil;
import com.wordplay.unit.starter.rbac.entity.Role;
import com.wordplay.unit.starter.rbac.mapper.RoleMapper;
import com.wordplay.unit.starter.rbac.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

	@Override
	public List<Role> getRolesByUserId(Long userId) {
		return this.baseMapper.getRolesByUserId(userId);
	}

	@Override
	public Leaf<Role> page(Role role) {
		Page<Role> page = new Page<>(role.getPageNum(), role.getPageSize());
		page = this.baseMapper.page(page, role);
		return LeafPageUtil.pageToLeaf(page);
	}

	@Override
	public List<Role> getAllRole() {
		List<Role> roleList = this.baseMapper.selectList(Wrappers.lambdaQuery(Role.class).orderByAsc(Role::getId));
		return roleList;
	}

}
