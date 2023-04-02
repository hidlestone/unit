package com.wordplay.unit.starter.mail.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wordplay.unit.starter.data.mp.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName(value = "s_mail_history")
public class MailHistory extends BaseEntity {

	private static final long serialVersionUID = -6833818198694766754L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 邮件模板ID
	 */
	@TableField(value = "template_id")
	private Long templateId;

	/**
	 * 邮件配置ID
	 */
	@TableField(value = "config_id")
	private Long configId;

	/**
	 * 标题
	 */
	@TableField(value = "title")
	private String title;

	/**
	 * 发送者
	 */
	@TableField(value = "`from`")
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
	 * 接收邮箱
	 */
	@TableField(value = "receive_mail")
	private String receiveMail;

	/**
	 * 抄送者
	 */
	@TableField(value = "cc")
	private String cc;

	/**
	 * 密送者
	 */
	@TableField(value = "bcc")
	private String bcc;

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
	@TableField(value = "send_flag")
	private Byte sendFlag;

}