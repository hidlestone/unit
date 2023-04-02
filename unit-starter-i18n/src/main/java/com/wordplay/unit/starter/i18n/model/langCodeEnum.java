package com.wordplay.unit.starter.i18n.model;

/**
 * 语言编码枚举类
 */
public enum langCodeEnum {

	en("en", "英文"),
	zh_CN("zh_CN", "简体中文"),
	zh_TW("zh_TW", "繁体中文");

	private String code;
	private String name;

	langCodeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
