package com.wordplay.unit.starter.mq.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.data.mp.util.LeafPageUtil;
import com.wordplay.unit.starter.mq.entity.MqTraceLog;
import com.wordplay.unit.starter.mq.mapper.MqTraceLogMapper;
import com.wordplay.unit.starter.mq.service.MqTraceLogService;
import org.springframework.stereotype.Service;

@Service
public class MqTraceLogServiceImpl extends ServiceImpl<MqTraceLogMapper, MqTraceLog> implements MqTraceLogService {

	@Override
	public Leaf<MqTraceLog> page(MqTraceLog mqTraceLog) {
		Page<MqTraceLog> page = new Page<>(mqTraceLog.getPageNum(), mqTraceLog.getPageSize());
		page = this.baseMapper.page(page, mqTraceLog);
		return LeafPageUtil.pageToLeaf(page);
	}

}
