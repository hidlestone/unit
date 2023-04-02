package com.wordplay.unit.starter.mail.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * 需要从不同的邮件账号发送
 *
 * @author zhuangpf
 */
public class FallMailAuthentication extends Authenticator {

	private PasswordAuthentication pa;

	/**
	 * 账号
	 */
	private String username;
	/**
	 * 密码
	 */
	private String password;

	public FallMailAuthentication(String username, String password) {
		this.username = username;
		this.password = password;
		pa = new PasswordAuthentication(username, password);
	}

	public PasswordAuthentication getPasswordAuthentication() {
		return pa;
	}

}
