package com.wordplay.unit.starter.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.sms.entity.SmsConfig;

public interface SmsConfigService extends IService<SmsConfig> {

	Leaf<SmsConfig> page(SmsConfig smsConfig);

}
