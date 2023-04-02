package com.wordplay.unit.starter.sysparam.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.data.mp.util.LeafPageUtil;
import com.wordplay.unit.starter.sysparam.entity.DictDtl;
import com.wordplay.unit.starter.sysparam.mapper.DictDtlMapper;
import com.wordplay.unit.starter.sysparam.service.DictDtlService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictDtlServiceImpl extends ServiceImpl<DictDtlMapper, DictDtl> implements DictDtlService {

	@Override
	public List<DictDtl> getDictDtlsByDictCode(String dictCode) {
		List<DictDtl> dictDtls = this.baseMapper.getDictDtlsByCode(dictCode);
		return dictDtls;
	}

	@Override
	public Leaf<DictDtl> page(DictDtl dictDtl) {
		Page<DictDtl> page = new Page<>(dictDtl.getPageNum(), dictDtl.getPageSize());
		page = this.baseMapper.page(page, dictDtl);
		return LeafPageUtil.pageToLeaf(page);
	}

}
