package com.wordplay.unit.starter.rbac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.rbac.entity.User;

public interface UserService extends IService<User> {

	Leaf<User> page(User user);

}
