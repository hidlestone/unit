package com.wordplay.unit.starter.core.context;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 当前请求上下文工具类
 *
 * @author zhuangpf
 * @date 2023-04-22
 */
public class CurrentContextHelper {

	// 本地线程，用于存储当前请求上下文信息
	private static final ThreadLocal<CurrentContext> CURRENT_CONTEXT = new InheritableThreadLocal();
	public static final Logger LOGGER = LoggerFactory.getLogger(CurrentContextHelper.class);

	/**
	 * 初始化当前请求上下文，com.wordplay.unit.starter.mvc.filter.CurrentContextFilter#doFilter
	 *
	 * @param userAuthInfo          用户登录授权信息
	 * @param request               HttpServletRequest
	 * @param requestTime           请求时间
	 * @param applicationInstanceId 应用实例ID
	 * @param serviceName           服务名
	 */
	public static void initCurrentContext(UserAuthInfo userAuthInfo, HttpServletRequest request, LocalDateTime requestTime, Long applicationInstanceId, String serviceName) {
		CURRENT_CONTEXT.set(new CurrentContext(userAuthInfo, request, requestTime, applicationInstanceId, serviceName));
	}

	/**
	 * 更新当前请求上下文
	 *
	 * @param userAuthInfo 用户登录授权信息
	 * @param request      HttpServletRequest
	 * @param requestTime  请求时间
	 */
	public static void updateCurrentContext(UserAuthInfo userAuthInfo, HttpServletRequest request, LocalDateTime requestTime) {
		CurrentContext currentContext = CURRENT_CONTEXT.get();
		currentContext.setUserAuthInfo(userAuthInfo);
		currentContext.resolveHttpHeaders(request);
		currentContext.setRequestTime(requestTime);
		currentContext.setUserAuthInfo(userAuthInfo);
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

	public static UserAuthInfo getUserAuthInfo() {
		return get() == null ? null : get().getUserAuthInfo();
	}

	/**
	 * 获取用户ID
	 *
	 * @return 用户ID
	 */
	public static Long getUserId() {
		if (get() == null) {
			return null;
		} else {
			return getUserAuthInfo() != null ? getUserAuthInfo().getId() : null;
		}
	}

	/**
	 * 获取用户名
	 *
	 * @return 用户名
	 */
	public static String getUserName() {
		return getUserAuthInfo() != null ? getUserAuthInfo().getUsername() : null;
	}

	/**
	 * 获取角色列表
	 *
	 * @return 角色列表
	 */
	public static List<String> getUserCodeList() {
		return getUserAuthInfo() != null ? getUserAuthInfo().getRoleCodeList() : null;
	}

	/**
	 * 是否是admin角色
	 *
	 * @return 是否是admin角色
	 */
	public static boolean isAdmin() {
		List<String> userCodeList = getUserCodeList();
		if (null != userCodeList && userCodeList.size() > 0) {
			return userCodeList.contains("admin");
		} else {
			return false;
		}
	}

	/**
	 * 获取请求ID
	 *
	 * @return 请求ID
	 */
	public static String getRequestId() {
		return get() == null ? null : get().getHeader(CoreStarterConstant.REQUEST_ID);
	}

	/**
	 * 获取客户端编码
	 *
	 * @return 客户端编码
	 */
	public static String getClientCode() {
		return get() == null ? null : get().getHeader(CoreStarterConstant.CLIENT_CODE);
	}

	/**
	 * 获取模块编码
	 *
	 * @return 模块编码
	 */
	public static String getModuleCode() {
		if (get() == null) {
			return null;
		} else {
			String moduleCode = get().getHeader(CoreStarterConstant.MODULE_CODE);
			return moduleCode;
		}
	}

	/**
	 * 获取请求时间
	 *
	 * @return 请求时间
	 */
	public static LocalDateTime getRequestTime() {
		return get() == null ? null : get().getRequestTime();
	}

	/**
	 * 获取token
	 *
	 * @return token
	 */
	public static String getToken() {
		if (get() == null) {
			return null;
		} else {
			String token = get().getHeader(CoreStarterConstant.TOKEN);
			return token;
		}
	}

	/**
	 * 获取用户真实IP
	 *
	 * @return 用户真实IP
	 */
	public static String getUserRealIp() {
		if (get() == null) {
			return null;
		} else {
			Map<String, String> headers = get().getHttpHeaders();
			Iterator iterator = headers.keySet().iterator();
			String name;
			String value;
			do {
				if (!iterator.hasNext()) {
					iterator = headers.keySet().iterator();
					do {
						if (!iterator.hasNext()) {
							try {
								return getRequest().getRemoteAddr();
							} catch (Exception var4) {
								return "";
							}
						}
						name = (String) iterator.next();
						value = headers.get(name);
					} while (!"x-forwarded-for".equalsIgnoreCase(name) || !StringUtils.isNotBlank(value));
					return value.split("@@@")[0];
				}
				name = (String) iterator.next();
				value = headers.get(name);
			} while (!"x-real-ip".equalsIgnoreCase(name) || !StringUtils.isNotBlank(value));
			return value;
		}
	}

	/**
	 * 获取HttpServletRequest对象
	 *
	 * @return HttpServletRequest
	 */
	public static HttpServletRequest getRequest() {
		try {
			ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			return servletRequestAttributes.getRequest();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @return 请求url
	 */
	public static String getUrl() {
		return get() == null ? null : get().getUrl();
	}

	public static Locale getLocale() {
		return CURRENT_CONTEXT.get() != null && (CURRENT_CONTEXT.get()).getLocale() != null ? (CURRENT_CONTEXT.get()).getLocale() : toLocale(getLanguageCode());
	}

	public static Locale toLocale(String locale) {
		if (!StringUtils.isBlank(locale) && !"null".equalsIgnoreCase(locale)) {
			if (locale.contains("_#")) {
				locale = locale.split("_#")[0];
			} else if (locale.contains("#")) {
				locale = locale.split("#")[0];
			}
			StringTokenizer st = new StringTokenizer(locale, "-_");
			return new Locale(st.nextToken(), st.hasMoreTokens() ? st.nextToken() : "", st.hasMoreTokens() ? st.nextToken() : "");
		} else {
			return null;
		}
	}

	/**
	 * 获取请求头语言编码
	 *
	 * @return 语言编码
	 */
	public static String getLanguageCode() {
		if (get() == null) {
			return "zh_CN";
		} else {
			String language = get().getHeader(CoreStarterConstant.LANGUAGE);
			if (StringUtils.isBlank(language)) {
				language = "zh_CN";
			}
			return language;
		}
	}

	/**
	 * 根据请求头获取值
	 *
	 * @param name 请求头名称
	 * @return 对应的值
	 */
	public static String getHeader(String name) {
		return get() == null ? null : get().getHeader(name);
	}

	public static List<String> getHeaders(String name) {
		return get() == null ? null : get().getHeaders(name);
	}

	public static Map<String, String> getHeaders() {
		return get() == null ? null : get().getHeaders();
	}

	public static String getParameter(String name) {
		return getRequest() != null ? getRequest().getParameter(name) : "";
	}

	public static String getTarget() {
		String target = getHeader(CoreStarterConstant.TARGET);
		if (StringUtils.isBlank(target) && getRequest() != null) {
			target = getParameter(CoreStarterConstant.TARGET);
		}
		return target;
	}

	public static String getRealDomain() {
		String forwardedHost = getHeader("x-forwarded-host");
		String forwardedProto = getHeader("x-forwarded-proto");
		if (StringUtils.isNotBlank(forwardedProto)) {
			forwardedProto = forwardedProto.split(",")[0].trim();
		} else {
			forwardedProto = getRequest() != null ? getRequest().getScheme() : "http";
		}
		if (StringUtils.isNotBlank(forwardedHost)) {
			forwardedHost = forwardedHost.split(",")[0].trim();
			return forwardedProto + "://" + forwardedHost;
		} else if (getRequest() != null) {
			String url = getRequest().getRequestURL().toString();
			String contextPath = url.replace(getRequest().getRequestURI(), "");
			return contextPath;
		} else {
			return "";
		}
	}

	/**
	 * 清除本地线程数据
	 */
	public static void cleanup() {
		CURRENT_CONTEXT.remove();
	}

}
