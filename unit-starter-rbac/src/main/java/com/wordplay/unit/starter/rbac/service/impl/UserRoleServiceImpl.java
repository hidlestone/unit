package com.wordplay.unit.starter.rbac.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.rbac.entity.UserRole;
import com.wordplay.unit.starter.rbac.mapper.UserRoleMapper;
import com.wordplay.unit.starter.rbac.service.UserRoleService;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}
