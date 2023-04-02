package com.wordplay.unit.starter.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.sms.entity.SmsTemplate;

public interface SmsTemplateService extends IService<SmsTemplate> {

	Leaf<SmsTemplate> page(SmsTemplate smsTemplate);

}
