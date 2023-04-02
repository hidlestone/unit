package com.wordplay.unit.starter.sysparam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wordplay.unit.starter.sysparam.entity.SysParamItem;

public interface SysParamItemMapper extends BaseMapper<SysParamItem> {

	Page<SysParamItem> page(Page<SysParamItem> page, SysParamItem sysParamItem);

}