package com.wordplay.unit.starter.mq.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wordplay.unit.starter.mq.entity.MqTraceLog;

public interface MqTraceLogMapper extends BaseMapper<MqTraceLog> {

	Page<MqTraceLog> page(Page<MqTraceLog> page, MqTraceLog mqTraceLog);

}