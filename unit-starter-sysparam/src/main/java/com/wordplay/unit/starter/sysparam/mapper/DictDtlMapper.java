package com.wordplay.unit.starter.sysparam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wordplay.unit.starter.sysparam.entity.DictDtl;

import java.util.List;

public interface DictDtlMapper extends BaseMapper<DictDtl> {

	Page<DictDtl> page(Page<DictDtl> page, DictDtl dictDtl);

	List<DictDtl> getDictDtlsByCode(String code);

}