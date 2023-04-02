package com.wordplay.unit.starter.sms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wordplay.unit.starter.sms.entity.SmsTemplate;

public interface SmsTemplateMapper extends BaseMapper<SmsTemplate> {

	Page<SmsTemplate> list(Page<SmsTemplate> page, SmsTemplate smsTemplate);

}