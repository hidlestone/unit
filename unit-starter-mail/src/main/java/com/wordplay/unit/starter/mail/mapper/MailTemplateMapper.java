package com.wordplay.unit.starter.mail.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wordplay.unit.starter.mail.entity.MailTemplate;

public interface MailTemplateMapper extends BaseMapper<MailTemplate> {

	Page<MailTemplate> page(Page<MailTemplate> page, MailTemplate mailTemplate);

}