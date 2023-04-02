package com.wordplay.unit.starter.rbac.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.data.mp.util.LeafPageUtil;
import com.wordplay.unit.starter.rbac.entity.Department;
import com.wordplay.unit.starter.rbac.mapper.DepartmentMapper;
import com.wordplay.unit.starter.rbac.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

	@Override
	public Leaf<Department> page(Department department) {
		Page<Department> page = new Page<>(department.getPageNum(), department.getPageSize());
		page = this.baseMapper.page(page, department);
		return LeafPageUtil.pageToLeaf(page);
	}

	@Override
	public List<Department> getDepartmentTree(Department department) {
		List<Department> departmentList = this.baseMapper.getDepartmentTree(department);
		return departmentList;
	}

	@Override
	public List<Department> getDepartmentsByParentId(Long parentId) {
		List<Department> departmentList = this.baseMapper.getDepartmentsByParentId(parentId);
		return departmentList;
	}

}
