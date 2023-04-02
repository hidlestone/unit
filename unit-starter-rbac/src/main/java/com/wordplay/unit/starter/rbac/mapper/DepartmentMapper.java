package com.wordplay.unit.starter.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wordplay.unit.starter.rbac.entity.Department;

import java.util.List;

public interface DepartmentMapper extends BaseMapper<Department> {

	Page<Department> page(Page<Department> page, Department department);

	List<Department> getDepartmentTree(Department department);

	List<Department> getDepartmentsByParentId(Long parentId);
}