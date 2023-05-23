package com.wordplay.unit.starter.core.context;

import com.wordplay.unit.starter.core.util.SnowFlakeGenerator;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 当前请求上下文
 *
 * @author zhuangpf
 * @date 2023-04-22
 */
public class CurrentContext implements Serializable {

	private static final long serialVersionUID = -2036290101176050771L;

	// 应用实例ID
	private Long applicationInstanceId;
	// 服务名称
	private String serviceName;
	// 用户登录授权信息
	private UserAuthInfo userAuthInfo;
	// 请求时间
	private LocalDateTime requestTime;
	// 请求url
	private String url;
	// 请求头
	private Map<String, String> httpHeaders;
	// locale
	private Locale locale;

	public CurrentContext(UserAuthInfo userAuthInfo, HttpServletRequest request, LocalDateTime requestTime, Long applicationInstanceId, String serviceName) {
		this.applicationInstanceId = applicationInstanceId;
		this.serviceName = serviceName;
		this.url = "/" + serviceName + (request.getRequestURI().startsWith("/") ? request.getRequestURI() : "/" + request.getRequestURI());
		this.userAuthInfo = userAuthInfo;
		this.requestTime = requestTime;
		this.resolveHttpHeaders(request);
		this.locale = CurrentContextHelper.toLocale(this.getHeader(CoreStarterConstant.LANGUAGE));
	}

	/**
	 * 解析请求头
	 *
	 * @param request HttpServletRequest
	 */
	public void resolveHttpHeaders(HttpServletRequest request) {
		this.httpHeaders = new HashMap();
		Enumeration headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = (String) headerNames.nextElement();
			Enumeration<String> values = request.getHeaders(headerName);
			ArrayList list = new ArrayList();
			while (values.hasMoreElements()) {
				list.add(values.nextElement());
			}
			this.httpHeaders.put(headerName, StringUtils.join(list, "@@@"));
		}
		// token
		if (StringUtils.isBlank(this.httpHeaders.get(CoreStarterConstant.TOKEN))) {
			this.httpHeaders.put(CoreStarterConstant.TOKEN, request.getParameter(CoreStarterConstant.TOKEN));
		}
		// client-code
		if (StringUtils.isBlank(this.httpHeaders.get(CoreStarterConstant.CLIENT_CODE))) {
			this.httpHeaders.put(CoreStarterConstant.CLIENT_CODE, request.getParameter(CoreStarterConstant.CLIENT_CODE));
		}
		// target
		if (StringUtils.isBlank(this.httpHeaders.get(CoreStarterConstant.TARGET))) {
			this.httpHeaders.put(CoreStarterConstant.TARGET, request.getParameter(CoreStarterConstant.TARGET));
		}
		// request-Id : 请求ID
		String requestId = this.getHeader(CoreStarterConstant.REQUEST_ID);
		if (StringUtils.isBlank(requestId)) {
			requestId = request.getParameter(CoreStarterConstant.REQUEST_ID);
			if (StringUtils.isBlank(requestId)) {
				requestId = String.valueOf((new SnowFlakeGenerator(this.applicationInstanceId)).nextId());
			}
		}
		// request-order
		this.httpHeaders.put(CoreStarterConstant.REQUEST_ID, requestId);
		String requestOrder = this.getHeader(CoreStarterConstant.REQUEST_ORDER);
		try {
			requestOrder = String.valueOf(Integer.valueOf(requestOrder) + 1);
		} catch (Exception e) {
			requestOrder = "1";
		}
		this.httpHeaders.put(CoreStarterConstant.REQUEST_ORDER, requestOrder);
	}

	/**
	 * 获取请求头(idx-0)
	 *
	 * @param name 请求头名称
	 * @return
	 */
	public String getHeader(String name) {
		if (this.httpHeaders == null) {
			return null;
		} else {
			String value = this.httpHeaders.get(name);
			if (StringUtils.isBlank(value)) {
				value = this.httpHeaders.get(name.toLowerCase());
			}
			return StringUtils.isNotBlank(value) ? value.split("@@@")[0] : null;
		}
	}

	/**
	 * 请求头对应的值列表
	 *
	 * @param name 请求头名称
	 * @return
	 */
	public ArrayList<String> getHeaders(String name) {
		String value = this.httpHeaders.get(name);
		if (StringUtils.isBlank(value)) {
			value = this.httpHeaders.get(name.toLowerCase());
		}
		return StringUtils.isNotBlank(value) ? new ArrayList(Arrays.asList(value.split("@@@"))) : null;
	}

	/**
	 * 获取所有请求头信息
	 *
	 * @return
	 */
	public Map<String, String> getHeaders() {
		return this.httpHeaders;
	}

	public Long getApplicationInstanceId() {
		return applicationInstanceId;
	}

	public void setApplicationInstanceId(Long applicationInstanceId) {
		this.applicationInstanceId = applicationInstanceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public UserAuthInfo getUserAuthInfo() {
		return userAuthInfo;
	}

	public void setUserAuthInfo(UserAuthInfo userAuthInfo) {
		this.userAuthInfo = userAuthInfo;
	}

	public LocalDateTime getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(LocalDateTime requestTime) {
		this.requestTime = requestTime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getHttpHeaders() {
		return httpHeaders;
	}

	public void setHttpHeaders(Map<String, String> httpHeaders) {
		this.httpHeaders = httpHeaders;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

}
