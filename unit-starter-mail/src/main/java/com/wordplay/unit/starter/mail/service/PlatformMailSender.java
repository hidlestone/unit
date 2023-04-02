package com.wordplay.unit.starter.mail.service;

import com.wordplay.unit.starter.api.response.ResponseResult;
import com.wordplay.unit.starter.mail.model.MailSendInfoDto;

/**
 * 平台邮件发送服务
 */
public interface PlatformMailSender {

	/**
	 * 发送简单邮件
	 *
	 * @param dto 邮件信息
	 * @return 是否发送成功
	 */
	ResponseResult sendSimpleEmail(MailSendInfoDto dto);

	/**
	 * html格式邮件
	 *
	 * @param dto 邮件信息
	 * @return 是否发送成功
	 */
	ResponseResult sendMimeMsgEmail(MailSendInfoDto dto);

	/**
	 * 发送行内邮件
	 *
	 * @param dto 邮件信息
	 * @return 是否发送成功
	 */
	ResponseResult sendInlineMail(MailSendInfoDto dto);

	/**
	 * 发送模板邮件
	 *
	 * @param dto 邮件信息
	 * @return 是否发送成功
	 */
	ResponseResult sendTemplateEmail(MailSendInfoDto dto);

}
