package com.wordplay.unit.starter.mq.rabbit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ默认配置 <br>
 * message 从 producer 到 rabbitmq broker cluster 则会返回一个 confirmCallback <br>
 * message 从 exchange 到 queue 投递失败则会返回一个 returnCallback <br>
 * confrim 模式只能保证消息到达 broker，不能保证消息准确投递到目标 queue 里。
 * 在有些业务场景下，我们需要保证消息一定要投递到目标 queue 里，此时就需要用到 return 退回模式。<br>
 * <p>
 * 利用这两个callback 控制消息的最终一致性和部分纠错能力 <br>
 * <p>
 * 配置项目中使用的：RabbitTemplate，一些使用参数
 *
 * @author zhuangpf
 */
@Configuration
public class RabbitMqConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqConfig.class);

	/**
	 * 构建RabbitTemplate
	 *
	 * @param connectionFactory 连接工厂
	 * @return rabbitTemplate
	 */
	@Bean
	public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
		// 开启投递确认confirm
		connectionFactory.setPublisherConfirms(true);
		// 开启未投递到queue退回模式
		connectionFactory.setPublisherReturns(true);
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		// 设置message序列化方法
		rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
		// 如果exchange根据自身类型和消息routingKey无法找到一个合适的queue存储消息，那么broker会调用basic.return方法将消息返还给生产者
		rabbitTemplate.setMandatory(true);
//		rabbitTemplate.setConfirmCallback(new DefaultConfirmCallback());
//		rabbitTemplate.setReturnCallback(new DefaultReturnCallback());
		return rabbitTemplate;
	}

	/**
	 * 替换mq默认的序列化器
	 */
	@Bean
	public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
		return new Jackson2JsonMessageConverter(objectMapper);
	}

}
