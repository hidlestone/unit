package com.wordplay.unit.starter.sysparam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wordplay.unit.starter.sysparam.entity.SysParamGroup;

import java.util.List;

public interface SysParamGroupMapper extends BaseMapper<SysParamGroup> {

	List<SysParamGroup> getAllSysParamGroup();

	Page<SysParamGroup> page(Page<SysParamGroup> page, SysParamGroup sysParamGroup);
}