package com.wordplay.unit.starter.sysparam.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.data.mp.util.LeafPageUtil;
import com.wordplay.unit.starter.sysparam.entity.SysParamItem;
import com.wordplay.unit.starter.sysparam.mapper.SysParamItemMapper;
import com.wordplay.unit.starter.sysparam.service.SysParamItemService;
import org.springframework.stereotype.Service;

@Service
public class SysParamItemServiceImpl extends ServiceImpl<SysParamItemMapper, SysParamItem> implements SysParamItemService {

	@Override
	public SysParamItem get(String code) {
		SysParamItem sysParamItem = getOne(Wrappers.lambdaQuery(SysParamItem.class).eq(SysParamItem::getCode, code));
		return sysParamItem;
	}

	@Override
	public Leaf<SysParamItem> page(SysParamItem sysParamItem) {
		Page<SysParamItem> page = new Page<>(sysParamItem.getPageNum(), sysParamItem.getPageSize());
		page = this.baseMapper.page(page, sysParamItem);
		return LeafPageUtil.pageToLeaf(page);
	}

}
