package com.wordplay.unit.starter.sms.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.data.mp.util.LeafPageUtil;
import com.wordplay.unit.starter.sms.entity.SmsConfig;
import com.wordplay.unit.starter.sms.mapper.SmsConfigMapper;
import com.wordplay.unit.starter.sms.service.SmsConfigService;
import org.springframework.stereotype.Service;

@Service
public class SmsConfigServiceImpl extends ServiceImpl<SmsConfigMapper, SmsConfig> implements SmsConfigService {

	@Override
	public Leaf<SmsConfig> page(SmsConfig smsConfig) {
		Page<SmsConfig> page = new Page<>(smsConfig.getPageNum(), smsConfig.getPageSize());
		page = this.baseMapper.list(page, smsConfig);
		return LeafPageUtil.pageToLeaf(page);
	}

}
