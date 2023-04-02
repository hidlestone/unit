package com.wordplay.unit.starter.shiro.model;

/**
 * 系统登录方式
 *
 * @author zhuangpf
 */
public enum LoginTypeEnum {

	/**
	 * 账号密码
	 */
	PASSWORD,
	/**
	 * 安全码
	 */
	VERIFICATIONCODE,
	/**
	 * 二维码
	 */
	QRCODE;

}
