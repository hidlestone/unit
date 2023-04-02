package com.wordplay.unit.starter.sysparam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.sysparam.entity.SysParamGroup;

/**
 * @author zhuangpf
 */
public interface SysParamGroupService extends IService<SysParamGroup> {

	SysParamGroup get(String code);

	Leaf<SysParamGroup> page(SysParamGroup sysParamGroup);

}
