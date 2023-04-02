package com.wordplay.unit.starter.rbac.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.data.mp.util.LeafPageUtil;
import com.wordplay.unit.starter.rbac.entity.Menu;
import com.wordplay.unit.starter.rbac.mapper.MenuMapper;
import com.wordplay.unit.starter.rbac.service.MenuService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

	@Override
	public Leaf<Menu> page(Menu menu) {
		Page<Menu> page = new Page<>(menu.getPageNum(), menu.getPageSize());
		page = this.baseMapper.page(page, menu);
		return LeafPageUtil.pageToLeaf(page);
	}

	@Override
	public List<Menu> getAllMenusByUserId(Long userId) {
		List<Menu> menuList = this.baseMapper.getAllMenusByUserId(userId);
		return menuList;
	}

	@Override
	public List<Menu> getMenuTree(Menu menu) {
		List<Menu> menuList = this.baseMapper.getMenuTree(menu);
		return menuList;
	}

	@Override
	public List<Menu> getMenusByParentId(Long parentId) {
		List<Menu> menuList = this.baseMapper.getMenusByParentId(parentId);
		return menuList;
	}

}
