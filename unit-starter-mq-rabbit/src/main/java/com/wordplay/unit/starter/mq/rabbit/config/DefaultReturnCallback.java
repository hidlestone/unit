package com.wordplay.unit.starter.mq.rabbit.config;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.wordplay.unit.starter.mq.entity.MqTraceLog;
import com.wordplay.unit.starter.mq.model.StageEnum;
import com.wordplay.unit.starter.mq.service.MqTraceLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * ReturnCallback 接口用于实现消息发送到 RabbitMQ 交换器, 但无相应队列与交换器绑定时的回调<br/>
 * 默认的消息投递到queue失败回退模式<br/>
 * 当消息不能被正确路由到某个queue时，会回调如下方法
 *
 * @author zhuangpf
 */
@Component
public class DefaultReturnCallback implements RabbitTemplate.ReturnCallback {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultReturnCallback.class);

	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Autowired
	private MqTraceLogService mqTraceLogService;

	@PostConstruct
	public void init() {
		rabbitTemplate.setReturnCallback(this);
	}


	@Override
	public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
		// 这样如果未能投递到目标 queue 里将调用 returnCallback ，可以记录下详细到投递数据，定期的巡检或者自动纠错都需要这些数据。
		LOGGER.info("[fall platform] send message to queue fial, message loss : exchange({}), route({}), replyCode({}), replyText({}), message:{}",
				exchange, routingKey, replyCode, replyText, message);
		String msg = new String(message.getBody());
		if (StringUtils.isBlank(msg)) {
			LOGGER.error("msg is not exist.");
			return;
		}
		MqTraceLog mqTraceLogTmp = JSON.parseObject(msg, MqTraceLog.class);
		// 更新该记录的状态
		MqTraceLog mqTraceLog = mqTraceLogService.getById(mqTraceLogTmp.getId());
		if (null == mqTraceLog) {
			LOGGER.error("mqTraceLog is not exist, id : {}", mqTraceLogTmp.getId());
		}
		mqTraceLog.setStage(StageEnum.DELIVER_QUEUE_FAIL);
		mqTraceLogService.updateById(mqTraceLog);
	}
	
}
