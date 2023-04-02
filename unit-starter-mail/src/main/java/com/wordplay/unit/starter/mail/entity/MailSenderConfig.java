package com.wordplay.unit.starter.mail.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wordplay.unit.starter.data.mp.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 将原本配置在yml的spring.mail，保存在数据库中
 *
 * @link org.springframework.boot.autoconfigure.mail.MailProperties
 */
@Getter
@Setter
@TableName(value = "s_mail_sender_config")
public class MailSenderConfig extends BaseEntity {

	private static final long serialVersionUID = 799272096665923741L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 如：smtp.163.com
	 */
	@TableField(value = "`host`")
	private String host;

	/**
	 * 端口
	 */
	@TableField(value = "port")
	private Integer port;

	/**
	 * 账号
	 */
	@TableField(value = "username")
	private String username;

	/**
	 * 密码
	 */
	@TableField(value = "password")
	private String password;

	/**
	 * 协议
	 */
	@TableField(value = "protocol")
	private String protocol = "smtp";

	/**
	 * 默认编码
	 */
	@TableField(value = "default_encoding")
	private String defaultEncoding = "UTF-8";
	//	private Charset defaultEncoding = StandardCharsets.UTF_8;

	/**
	 * 其他的参数配置(JSON格式)
	 */
	@TableField(value = "properties")
	private String properties;

}