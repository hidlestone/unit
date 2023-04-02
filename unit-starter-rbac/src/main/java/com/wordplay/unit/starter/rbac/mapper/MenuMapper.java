package com.wordplay.unit.starter.rbac.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wordplay.unit.starter.rbac.entity.Menu;

import java.util.List;

public interface MenuMapper extends BaseMapper<Menu> {

	Page<Menu> page(Page<Menu> page, Menu menu);

	List<Menu> getAllMenusByUserId(Long userId);

	List<Menu> getMenuTree(Menu menu);

	List<Menu> getMenusByParentId(Long parentId);
}