package com.wordplay.unit.starter.mq.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.mq.entity.MqTraceLog;

public interface MqTraceLogService extends IService<MqTraceLog> {

	Leaf<MqTraceLog> page(MqTraceLog mqTraceLog);

}
