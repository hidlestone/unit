package com.wordplay.unit.starter.mail.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.data.mp.util.LeafPageUtil;
import com.wordplay.unit.starter.mail.entity.MailSenderConfig;
import com.wordplay.unit.starter.mail.mapper.MailSenderConfigMapper;
import com.wordplay.unit.starter.mail.service.MailSenderConfigService;
import org.springframework.stereotype.Service;

@Service
public class MailSenderConfigServiceImpl extends ServiceImpl<MailSenderConfigMapper, MailSenderConfig> implements MailSenderConfigService {

	@Override
	public Leaf<MailSenderConfig> page(MailSenderConfig mailSenderConfig) {
		Page<MailSenderConfig> page = new Page<>(mailSenderConfig.getPageNum(), mailSenderConfig.getPageSize());
		page = this.baseMapper.page(page, mailSenderConfig);
		return LeafPageUtil.pageToLeaf(page);
	}

}
