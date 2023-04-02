package com.wordplay.unit.starter.sms.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.data.mp.util.LeafPageUtil;
import com.wordplay.unit.starter.sms.entity.SmsTemplate;
import com.wordplay.unit.starter.sms.mapper.SmsTemplateMapper;
import com.wordplay.unit.starter.sms.service.SmsTemplateService;
import org.springframework.stereotype.Service;

@Service
public class SmsTemplateServiceImpl extends ServiceImpl<SmsTemplateMapper, SmsTemplate> implements SmsTemplateService {

	@Override
	public Leaf<SmsTemplate> page(SmsTemplate smsTemplate) {
		Page<SmsTemplate> page = new Page<>(smsTemplate.getPageNum(), smsTemplate.getPageSize());
		page = this.baseMapper.list(page, smsTemplate);
		return LeafPageUtil.pageToLeaf(page);
	}

}
