package com.wordplay.unit.starter.rbac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.rbac.entity.Department;

import java.util.List;

public interface DepartmentService extends IService<Department> {

	Leaf<Department> page(Department department);

	List<Department> getDepartmentTree(Department department);

	List<Department> getDepartmentsByParentId(Long parentId);
}
