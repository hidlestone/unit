package com.wordplay.unit.starter.mq.model;

/**
 * 消息处理阶段
 */
public enum StageEnum {

	/**
	 * 消息投递中
	 */
	DELIVERING,
	/**
	 * 投递到交换机成功
	 */
	DELIVER_EXCHANGE_SUCCESS,
	/**
	 * 投递到交换机失败
	 */
	DELIVER_EXCHANGE_FAIL,
	/**
	 * 投递到队列成功
	 */
	DELIVER_QUEUE_SUCCESS,
	/**
	 * 投递到队列失败
	 */
	DELIVER_QUEUE_FAIL,
	/**
	 * 已消费
	 */
	CONSUMED_SUCCESS,
	/**
	 * 消费失败
	 */
	CONSUMED_FAIL;

}
