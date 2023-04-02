package com.wordplay.unit.starter.mail.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.data.mp.util.LeafPageUtil;
import com.wordplay.unit.starter.mail.entity.MailHistory;
import com.wordplay.unit.starter.mail.mapper.MailHistoryMapper;
import com.wordplay.unit.starter.mail.service.MailHistoryService;
import org.springframework.stereotype.Service;

@Service
public class MailHistoryServiceImpl extends ServiceImpl<MailHistoryMapper, MailHistory> implements MailHistoryService {

	@Override
	public Leaf<MailHistory> page(MailHistory mailHistory) {
		Page<MailHistory> page = new Page<>(mailHistory.getPageNum(), mailHistory.getPageSize());
		page = this.baseMapper.page(page, mailHistory);
		return LeafPageUtil.pageToLeaf(page);
	}
}
