package com.wordplay.unit.starter.sms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wordplay.unit.starter.sms.entity.SmsHistory;

public interface SmsHistoryMapper extends BaseMapper<SmsHistory> {

	Page<SmsHistory> list(Page<SmsHistory> page, SmsHistory smsHistory);

}