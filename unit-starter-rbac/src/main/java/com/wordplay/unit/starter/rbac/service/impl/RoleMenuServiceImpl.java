package com.wordplay.unit.starter.rbac.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.rbac.entity.RoleMenu;
import com.wordplay.unit.starter.rbac.mapper.RoleMenuMapper;
import com.wordplay.unit.starter.rbac.service.RoleMenuService;
import org.springframework.stereotype.Service;

@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

}
