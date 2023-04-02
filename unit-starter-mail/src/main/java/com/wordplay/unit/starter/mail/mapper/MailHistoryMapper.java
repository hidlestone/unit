package com.wordplay.unit.starter.mail.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wordplay.unit.starter.mail.entity.MailHistory;

public interface MailHistoryMapper extends BaseMapper<MailHistory> {

	Page<MailHistory> page(Page<MailHistory> page, MailHistory mailHistory);

}