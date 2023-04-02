package com.wordplay.unit.starter.mq.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wordplay.unit.starter.data.mp.BaseEntity;
import com.wordplay.unit.starter.mq.model.StageEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName(value = "s_mq_trace_log")
public class MqTraceLog extends BaseEntity {

	private static final long serialVersionUID = -7778050526643997145L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 阶段
	 */
	@TableField(value = "stage")
	private StageEnum stage;

	/**
	 * 消息体JSON
	 */
	@TableField(value = "content")
	private String content;

	/**
	 * 交换机
	 */
	@TableField(value = "exchange")
	private String exchange;

	/**
	 * 路由
	 */
	@TableField(value = "route_key")
	private String routeKey;

	/**
	 * 投递TAG
	 */
	@TableField(value = "delivery_tag")
	private String deliveryTag;

	/**
	 * 消费TAG
	 */
	@TableField(value = "consumer_tag")
	private String consumerTag;

	/**
	 * ACK模式
	 */
	@TableField(value = "ack_mode")
	private String ackMode;

	/**
	 * 发布时间
	 */
	@TableField(value = "publish_time")
	private Date publishTime;

	/**
	 * 消费时间
	 */
	@TableField(value = "consume_time")
	private Date consumeTime;

	/**
	 * 订阅者
	 */
	@TableField(value = "subscriber")
	private String subscriber;

	/**
	 * 请求ID
	 */
	@TableField(value = "request_id")
	private Long requestId;

	/**
	 * 状态：0-失败，1-成功
	 */
	@TableField(value = "status")
	private Byte status;

	/**
	 * 开始发布时间
	 */
	@TableField(exist = false)
	private Date publishTimeStart;

	/**
	 * 结束发布时间
	 */
	@TableField(exist = false)
	private Date publishTimeEnd;

	/**
	 * 开始消费时间
	 */
	@TableField(exist = false)
	private Date consumeTimeStart;

	/**
	 * 结束消费时间
	 */
	@TableField(exist = false)
	private Date consumeTimeEnd;

}