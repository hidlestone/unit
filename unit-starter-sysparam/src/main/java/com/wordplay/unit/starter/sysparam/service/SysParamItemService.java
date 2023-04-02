package com.wordplay.unit.starter.sysparam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.sysparam.entity.SysParamItem;

public interface SysParamItemService extends IService<SysParamItem> {

	SysParamItem get(String code);

	Leaf<SysParamItem> page(SysParamItem sysParamItem);

}
