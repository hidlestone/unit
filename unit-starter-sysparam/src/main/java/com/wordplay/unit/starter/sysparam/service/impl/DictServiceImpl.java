package com.wordplay.unit.starter.sysparam.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.api.response.ResponseResult;
import com.wordplay.unit.starter.data.mp.util.LeafPageUtil;
import com.wordplay.unit.starter.sysparam.entity.Dict;
import com.wordplay.unit.starter.sysparam.mapper.DictMapper;
import com.wordplay.unit.starter.sysparam.service.DictDtlService;
import com.wordplay.unit.starter.sysparam.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

	@Autowired
	private DictDtlService dictDtlService;

	@Override
	public ResponseResult saveDict(Dict dict) {
		// 保存字典项
		this.save(dict);
		dict.getDictDtlList().forEach(e -> e.setDictId(dict.getId()));
		// 保存字典明细
		dictDtlService.saveBatch(dict.getDictDtlList());
		return ResponseResult.success();
	}

	@Override
	public Leaf<Dict> page(Dict dict) {
		Page<Dict> page = new Page<>(dict.getPageNum(), dict.getPageSize());
		page = this.baseMapper.page(page, dict);
		return LeafPageUtil.pageToLeaf(page);
	}

	@Override
	public List<Dict> getAllDicts() {
		List<Dict> dicts = this.baseMapper.selectList(null);
		return dicts;
	}

}
