package com.wordplay.unit.starter.mail.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.mail.entity.MailHistory;

public interface MailHistoryService extends IService<MailHistory> {

	Leaf<MailHistory> page(MailHistory mailHistory);

}
