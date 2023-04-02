package com.wordplay.unit.starter.sysparam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.api.response.ResponseResult;
import com.wordplay.unit.starter.sysparam.entity.Dict;

import java.util.List;

public interface DictService extends IService<Dict> {

	ResponseResult saveDict(Dict dict);

	Leaf<Dict> page(Dict dict);

	List<Dict> getAllDicts();

}
