package com.wordplay.unit.starter.api.response;

/**
 * 通用操作代码枚举类。<br>
 * 各个模块再进行独立的操作码的封装。
 *
 * @uthor payn
 */
public enum CommonCode implements ResultCode {
	SUCCESS(true, "0000", "操作成功"),
	FAIL(false, "500", "操作失败"),
	UNAUTHENTICATED(false, "1001", "此操作需要登陆系统"),
	UNAUTHORISE(false, "1002", "权限不足，无权操作"),
	ILLEGAL_PARAM(false, "1003", "非法参数"),
	UNSUPPORTED_METHOD(false, "1004", "请求方式不支持"),
	SERVER_ERROR(false, "1005", "抱歉，系统繁忙，请稍后重试");

	// 操作是否成功
	boolean success;
	// 操作代码
	String code;
	// 提示信息
	String message;

	CommonCode(boolean success, String code, String message) {
		this.success = success;
		this.code = code;
		this.message = message;
	}

	@Override
	public boolean success() {
		return success;
	}

	@Override
	public String code() {
		return code;
	}

	@Override
	public String message() {
		return message;
	}

	@Override
	public String toString() {
		return "CommonCode{" +
				"success=" + success +
				", code='" + code + '\'' +
				", message='" + message + '\'' +
				'}';
	}
}
