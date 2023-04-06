package com.wordplay.unit.starter.rbac.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.data.mp.util.LeafPageUtil;
import com.wordplay.unit.starter.rbac.entity.User;
import com.wordplay.unit.starter.rbac.mapper.UserMapper;
import com.wordplay.unit.starter.rbac.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

	@Override
	public Leaf<User> page(User user) {
		Page<User> page = new Page<>(user.getPageNum(), user.getPageSize());
		page = this.baseMapper.page(page, user);
		return LeafPageUtil.pageToLeaf(page);
	}

}
