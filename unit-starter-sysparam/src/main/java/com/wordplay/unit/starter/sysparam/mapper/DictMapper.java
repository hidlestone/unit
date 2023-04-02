package com.wordplay.unit.starter.sysparam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wordplay.unit.starter.sysparam.entity.Dict;

public interface DictMapper extends BaseMapper<Dict> {

	Page<Dict> page(Page<Dict> page, Dict dict);

}