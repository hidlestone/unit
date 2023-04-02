package com.wordplay.unit.starter.sms.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.data.mp.util.LeafPageUtil;
import com.wordplay.unit.starter.sms.entity.SmsHistory;
import com.wordplay.unit.starter.sms.mapper.SmsHistoryMapper;
import com.wordplay.unit.starter.sms.service.SmsHistoryService;
import org.springframework.stereotype.Service;

@Service
public class SmsHistoryServiceImpl extends ServiceImpl<SmsHistoryMapper, SmsHistory> implements SmsHistoryService {

	@Override
	public Leaf<SmsHistory> page(SmsHistory smsHistory) {
		Page<SmsHistory> page = new Page<>(smsHistory.getPageNum(), smsHistory.getPageSize());
		page = this.baseMapper.list(page, smsHistory);
		return LeafPageUtil.pageToLeaf(page);
	}
	
}
