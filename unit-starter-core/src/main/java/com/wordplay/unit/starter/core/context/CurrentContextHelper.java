package com.wordplay.unit.starter.core.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author zhuangpf
 * @date 2023-04-22
 */
public class CurrentContextHelper {

	// 本地线程
	private static final ThreadLocal<CurrentContext> CURRENT_CONTEXT = new InheritableThreadLocal();
	public static final Logger LOGGER = LoggerFactory.getLogger(CurrentContextHelper.class);

	/**
	 * 初始化当前请求上下文
	 *
	 * @param userAuthInfo          用户登录授权信息
	 * @param moduleCode            模块编码
	 * @param request               HttpServletRequest
	 * @param requestTime           请求时间
	 * @param applicationInstanceId 应用实例ID
	 * @param serviceName           服务名
	 */
	public static void initCurrentContext(UserAuthInfo userAuthInfo, String moduleCode, HttpServletRequest request, LocalDateTime requestTime, Long applicationInstanceId, String serviceName) {
		CURRENT_CONTEXT.set(new CurrentContext(userAuthInfo, moduleCode, request, requestTime, applicationInstanceId, serviceName));
	}

	/**
	 * 更新当前请求上下文
	 *
	 * @param userAuthInfo 用户登录授权信息
	 * @param moduleCode   模块编码
	 * @param request      HttpServletRequest
	 * @param requestTime  请求时间
	 */
	public static void updateCurrentContext(UserAuthInfo userAuthInfo, String moduleCode, HttpServletRequest request, LocalDateTime requestTime) {
		CurrentContext currentContext = CURRENT_CONTEXT.get();
		currentContext.setUserAuthInfo(userAuthInfo);
		currentContext.resolveHttpHeaders(request);
		currentContext.setRequestTime(requestTime);
		currentContext.setUserAuthInfo(userAuthInfo);
		currentContext.setModuleCodeAndFieldPermission(moduleCode);
	}

	/**
	 * 获取当前请求上下文
	 *
	 * @return 当前请求上下文
	 */
	public static CurrentContext get() {
		CurrentContext currentContext = CURRENT_CONTEXT.get();
		return currentContext;
	}

	/**
	 * 设置当前请求上下文
	 *
	 * @param currentContext 当前请求上下文
	 */
	public static void set(CurrentContext currentContext) {
		CURRENT_CONTEXT.set(currentContext);
	}


}
