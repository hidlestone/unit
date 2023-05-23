package com.wordplay.unit.starter.mvc.filter;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.wordplay.unit.starter.mvc.constant.MvcStarterConstant;
import com.wordplay.unit.starter.mvc.util.CookieFilterUtil;
import com.wordplay.unit.starter.sysparam.model.SysParamGroupEnum;
import com.wordplay.unit.starter.sysparam.util.SysParamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author zhuangpf
 * @date 2023-05-18
 */
public class XSSFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(XSSFilter.class);

	// 系统参数工具
	private final SysParamUtil sysParamUtil;
	// antPathMatcher路径匹配
	private AntPathMatcher antPathMatcher = new AntPathMatcher();

	// 是否开启XSS过滤
	private final Boolean XSS_ENABLE;
	// 排除URL规则
	private final String EXCLUDE_PATTERN;
	// 包括URL规则
	private final String INCLUDE_PATTERN;

	private final List<String> excludePatternList;
	private final List<String> includePatternlist;

	/**
	 * 构造函数
	 *
	 * @param sysParamUtil 系统参数工具
	 */
	public XSSFilter(SysParamUtil sysParamUtil) {
		this.sysParamUtil = sysParamUtil;
		// 从缓存中获取配置参数
		Map<String, String> sysItemMap = sysParamUtil.getSysParamGroupItemMap(SysParamGroupEnum.XSS_CONFIG.toString()).getData();
		XSS_ENABLE = Boolean.valueOf(sysParamUtil.mapGet(sysItemMap, MvcStarterConstant.XSS_ENABLE));
		// 按照,分割
		EXCLUDE_PATTERN = sysParamUtil.mapGet(sysItemMap, MvcStarterConstant.EXCLUDE_PATTERN);
		INCLUDE_PATTERN = sysParamUtil.mapGet(sysItemMap, MvcStarterConstant.INCLUDE_PATTERN);
		// 转成数组
		String[] excludePatternArr = EXCLUDE_PATTERN.split(",");
		excludePatternList = new ArrayList<>(excludePatternArr.length);
		String[] includePatternArr = INCLUDE_PATTERN.split(",");
		includePatternlist = new ArrayList<>(includePatternArr.length);
		// 存入列表
		Collections.addAll(excludePatternList, excludePatternArr);
		Collections.addAll(includePatternlist, includePatternArr);
		LOGGER.info("XSS CONFIG : " + JSON.toJSONString(sysItemMap));
	}

	/**
	 * 初始化
	 */
	@Override
	public void init(FilterConfig filterConfig) {
		LOGGER.debug("XSSFilter init.");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		// 内部请求 或者 不需要过滤的资源，则放行
		if (isInternalCall(request) || !this.isNeedXXSFilterRequest(request)) {
			chain.doFilter(request, response);
		}
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; ++i) {
				Cookie cookie = cookies[i];
				if ((null != cookie.getValue()) && CookieFilterUtil.isValidate(cookie.getValue().toLowerCase())) {
					throw new RuntimeException("cookie name - " + cookie.getName() + ":" + cookie.getValue() + " exist script!");
				}
			}
		}
		chain.doFilter(request, response);
	}

	/**
	 * 是否是内部请求
	 *
	 * @param request 请求
	 * @return
	 */
	private boolean isInternalCall(HttpServletRequest request) {
		String callType = request.getHeader("calltype");
		return "inner".equals(callType);
	}

	/**
	 * 是否是需要xss过滤的请求
	 *
	 * @param request 请求
	 * @return 是否是需要xss过滤的请求
	 */
	protected boolean isNeedXXSFilterRequest(HttpServletRequest request) {
		String url = request.getRequestURI();
		if (!XSS_ENABLE) {
			return false;
		}
		if (CollectionUtil.isEmpty(includePatternlist) || !this.match(url, includePatternlist)) {
			return false;
		}
		if (CollectionUtil.isNotEmpty(excludePatternList) && this.match(url, excludePatternList)) {
			return false;
		}
		return true;
	}

	/**
	 * antPathMatcher规则是否匹配
	 */
	protected boolean match(String url, List<String> patterns) {
		for (String pattern : patterns) {
			if ((this.antPathMatcher.match(pattern, url) && this.antPathMatcher.match("/" + pattern, url))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void destroy() {
		LOGGER.debug("XSSFilter destroy.");
	}

}
