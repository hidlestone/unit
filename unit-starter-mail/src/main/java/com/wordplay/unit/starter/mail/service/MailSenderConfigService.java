package com.wordplay.unit.starter.mail.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.mail.entity.MailSenderConfig;

public interface MailSenderConfigService extends IService<MailSenderConfig> {

	Leaf<MailSenderConfig> page(MailSenderConfig mailSenderConfig);

}
