package com.wordplay.unit.starter.mail.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.data.mp.util.LeafPageUtil;
import com.wordplay.unit.starter.mail.entity.MailTemplate;
import com.wordplay.unit.starter.mail.mapper.MailTemplateMapper;
import com.wordplay.unit.starter.mail.service.MailTemplateService;
import org.springframework.stereotype.Service;

@Service
public class MailTemplateServiceImpl extends ServiceImpl<MailTemplateMapper, MailTemplate> implements MailTemplateService {

	@Override
	public Leaf<MailTemplate> page(MailTemplate mailTemplate) {
		Page<MailTemplate> page = new Page<>(mailTemplate.getPageNum(), mailTemplate.getPageSize());
		page = this.baseMapper.page(page, mailTemplate);
		return LeafPageUtil.pageToLeaf(page);
	}

}
