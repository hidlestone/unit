package com.wordplay.unit.starter.mail.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wordplay.unit.starter.mail.entity.MailSenderConfig;

public interface MailSenderConfigMapper extends BaseMapper<MailSenderConfig> {

	Page<MailSenderConfig> page(Page<MailSenderConfig> page, MailSenderConfig mailSenderConfig);
}