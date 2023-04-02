package com.wordplay.unit.starter.rbac.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.rbac.entity.RolePermission;
import com.wordplay.unit.starter.rbac.mapper.RolePermissionMapper;
import com.wordplay.unit.starter.rbac.service.RolePermissionService;
import org.springframework.stereotype.Service;

@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {

}
