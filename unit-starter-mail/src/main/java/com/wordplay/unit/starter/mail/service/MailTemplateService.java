package com.wordplay.unit.starter.mail.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.mail.entity.MailTemplate;

public interface MailTemplateService extends IService<MailTemplate> {

	Leaf<MailTemplate> page(MailTemplate mailTemplate);

}
