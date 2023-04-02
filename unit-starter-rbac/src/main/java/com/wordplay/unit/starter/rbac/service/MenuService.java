package com.wordplay.unit.starter.rbac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.rbac.entity.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {

	Leaf<Menu> page(Menu menu);

	List<Menu> getAllMenusByUserId(Long userId);

	List<Menu> getMenuTree(Menu menu);

	List<Menu> getMenusByParentId(Long parentId);

}
