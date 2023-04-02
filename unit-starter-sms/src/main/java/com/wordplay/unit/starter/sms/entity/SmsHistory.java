package com.wordplay.unit.starter.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wordplay.unit.starter.api.model.StatusEnum;
import com.wordplay.unit.starter.data.mp.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName(value = "s_sms_history")
public class SmsHistory extends BaseEntity {

	private static final long serialVersionUID = 5935905121436264723L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 短信模板ID
	 */
	@TableField(value = "template_id")
	private Long templateId;

	/**
	 * 邮件配置ID
	 */
	@TableField(value = "config_id")
	private Long configId;

	/**
	 * 发送者
	 */
	@TableField(value = "from")
	private String from;

	/**
	 * 接收用户ID
	 */
	@TableField(value = "receive_user_id")
	private Long receiveUserId;

	/**
	 * 接收用户名称
	 */
	@TableField(value = "receive_user_name")
	private String receiveUserName;

	/**
	 * 接收号码
	 */
	@TableField(value = "receive_phone_number")
	private String receivePhoneNumber;

	/**
	 * 内容
	 */
	@TableField(value = "content")
	private String content;

	/**
	 * 文件组ID
	 */
	@TableField(value = "file_group_id")
	private Long fileGroupId;

	/**
	 * 发送次数
	 */
	@TableField(value = "try_count")
	private Byte tryCount;

	/**
	 * 失败原因
	 */
	@TableField(value = "msg")
	private String msg;

	/**
	 * 最后一次发送时间
	 */
	@TableField(value = "last_send_time")
	private Date lastSendTime;

	/**
	 * 0-失败，1-成功
	 */
	@TableField(value = "`status`")
	private StatusEnum status;

	/**
	 * 上次发送时间开始
	 */
	@TableField(exist = false)
	private Date lastSendTimeStart;

	/**
	 * 上次发送时间结束
	 */
	@TableField(exist = false)
	private Date lastSendTimeEnd;

}