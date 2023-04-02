package com.wordplay.unit.starter.sysparam.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.data.mp.util.LeafPageUtil;
import com.wordplay.unit.starter.sysparam.entity.SysParamGroup;
import com.wordplay.unit.starter.sysparam.entity.SysParamItem;
import com.wordplay.unit.starter.sysparam.mapper.SysParamGroupMapper;
import com.wordplay.unit.starter.sysparam.mapper.SysParamItemMapper;
import com.wordplay.unit.starter.sysparam.service.SysParamGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统参数组服务
 *
 * @author zhuangpf
 */
@Service
public class SysParamGroupServiceImpl extends ServiceImpl<SysParamGroupMapper, SysParamGroup> implements SysParamGroupService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SysParamGroupServiceImpl.class);

	@Autowired
	private SysParamItemMapper sysParamItemMapper;

	@Override
	public SysParamGroup get(String code) {
		// 系统参数组
		SysParamGroup sysParamGroup = getOne(Wrappers.lambdaQuery(SysParamGroup.class).eq(SysParamGroup::getCode, code));
		if (null == sysParamGroup) {
			return null;
		}
		// 明细
		List<SysParamItem> sysParamItems = sysParamItemMapper.selectList(Wrappers.lambdaQuery(SysParamItem.class).eq(SysParamItem::getGroupCode, code));
		sysParamGroup.setSysParamItems(sysParamItems);
		return sysParamGroup;
	}

	@Override
	public Leaf<SysParamGroup> page(SysParamGroup sysParamGroup) {
		Page<SysParamGroup> page = new Page<>(sysParamGroup.getPageNum(), sysParamGroup.getPageSize());
		page = this.baseMapper.page(page, sysParamGroup);
		return LeafPageUtil.pageToLeaf(page);
	}

}
