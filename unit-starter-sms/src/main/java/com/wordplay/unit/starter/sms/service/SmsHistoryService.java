package com.wordplay.unit.starter.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.sms.entity.SmsHistory;

public interface SmsHistoryService extends IService<SmsHistory> {

	Leaf<SmsHistory> page(SmsHistory smsHistory);

}
