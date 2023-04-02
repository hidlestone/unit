package com.wordplay.unit.starter.rbac.util;

import com.wordplay.unit.starter.rbac.constant.RbacStarterConstant;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhuangpf
 */
public class RequestContexUtil {

	/**
	 * 在返回的请求头中设置token信息
	 *
	 * @param accesstoken  访问token
	 * @param refreshtoken 刷新token
	 */
	public static void setTokenHeader(String accesstoken, String refreshtoken) {
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		response.setHeader(RbacStarterConstant.ACCESSTOKEN, accesstoken);
		response.setHeader(RbacStarterConstant.REFRESHTOKEN, refreshtoken);
	}

	/**
	 * 获取访问token
	 */
	public static String getAccesstoken() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return request.getHeader(RbacStarterConstant.ACCESSTOKEN);
	}

	/**
	 * 获取刷新token
	 */
	public static String getRefreshtoken() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return request.getHeader(RbacStarterConstant.REFRESHTOKEN);
	}

	/**
	 * 获取请求头中的值
	 *
	 * @param header 请求头
	 * @return 请求头中的值
	 */
	public static String getRequestHeader(String header) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return request.getHeader(header);
	}

}
