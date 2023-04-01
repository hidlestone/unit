package com.wordplay.unit.starter.api.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 系统统一响应结果:<br>
 * 请求成功：<br>
 * success：true，code：CommonCode.SUCCESS.code()，message： CommonCode.SUCCESS.message()
 * <p>
 * 请求失败：<br>
 * success：false，
 * code：公共的使用 CommonCode.FAIL.code() <br>
 * 其他特定模块或子系统可以定义自己的响应码及响应信息，参照：CommonCode
 *
 * @author zhuangpf
 */
@Data
public class ResponseResult<T> implements Serializable {

	private static final long serialVersionUID = -2477262048060371180L;

	// 操作是否成功
	private boolean success;
	// 操作码
	private String code;
	// 返回信息
	private String message;
	// 响应结果，使用泛型，便于生成Swagger文档
	private T data;
	// 其他额外信息
	private Map<Object, Object> extra;

	public ResponseResult(boolean success, String code, String message, T data, Map<Object, Object> extra) {
		this.success = success;
		this.code = code;
		this.message = message;
		this.data = data;
		this.extra = extra;
	}

	public static <T> ResponseResult<T> success() {
		return success((String) null, (Object) null, (Map) null);
	}

	public static <T> ResponseResult<T> success(String message) {
		return success(message, (Object) null, (Map) null);
	}

	public static <T> ResponseResult<T> success(T result) {
		return new ResponseResult<T>(CommonCode.SUCCESS.success, CommonCode.SUCCESS.code(), CommonCode.SUCCESS.message, result, null);
	}

	private static <T> ResponseResult<T> success(String message, Object result, Map extra) {
		return new ResponseResult<T>(CommonCode.SUCCESS.success, CommonCode.SUCCESS.code(), message, (T) result, extra);
	}

	private static <T> ResponseResult<T> success(ResultCode resultCode) {
		return new ResponseResult<T>(resultCode.success(), resultCode.code(), resultCode.message(), null, null);
	}

	public static <T> ResponseResult<T> fail() {
		return fail(CommonCode.FAIL.code, null);
	}

	public static <T> ResponseResult<T> fail(String message) {
		return fail(CommonCode.FAIL.code, message);
	}

	public static <T> ResponseResult<T> fail(String code, String message) {
		return new ResponseResult<T>(CommonCode.FAIL.success, code, message, null, null);
	}

	public static <T> ResponseResult<T> fail(String code, String message, T result) {
		return new ResponseResult<T>(CommonCode.FAIL.success, code, message, result, null);
	}

	public static <T> ResponseResult<T> fail(String code, String message, T result, Map<Object, Object> extra) {
		return new ResponseResult<T>(CommonCode.FAIL.success, code, message, result, extra);
	}

	public static <T> ResponseResult<T> fail(ResultCode resultCode) {
		return new ResponseResult<T>(resultCode.success(), resultCode.code(), resultCode.message(), null, null);
	}

	/**
	 * 是否成功
	 */
	public boolean isSuccess() {
		return ((true == this.success) || (CommonCode.SUCCESS.code.equals(this.code))) ? true : false;
	}

}
