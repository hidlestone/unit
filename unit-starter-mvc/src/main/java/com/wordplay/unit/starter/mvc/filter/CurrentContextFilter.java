package com.wordplay.unit.starter.mvc.filter;

import com.wordplay.unit.starter.api.constant.ApiConstant;
import com.wordplay.unit.starter.cache.redis.util.RedisUtil;
import com.wordplay.unit.starter.core.context.CoreStarterConstant;
import com.wordplay.unit.starter.core.context.CurrentContextHelper;
import com.wordplay.unit.starter.core.context.UserAuthInfo;
import com.wordplay.unit.starter.mvc.constant.MvcStarterConstant;
import com.wordplay.unit.starter.mvc.util.ApplicationInstanceManager;
import com.wordplay.unit.starter.mvc.util.JWTUtil;
import com.wordplay.unit.starter.mvc.util.UserPermissionUtil;
import com.wordplay.unit.starter.rbac.constant.RbacStarterConstant;
import com.wordplay.unit.starter.rbac.entity.User;
import com.wordplay.unit.starter.rbac.model.TokenTypeEnum;
import com.wordplay.unit.starter.sysparam.model.SysParamGroupEnum;
import com.wordplay.unit.starter.sysparam.util.SysParamUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * 当前上下文过滤器
 *
 * @author zhuangpf
 * @date 2023-05-21
 */
public class CurrentContextFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(CurrentContextFilter.class);

	// 系统参数工具
	private final SysParamUtil sysParamUtil;
	private final UserPermissionUtil userPermissionUtil;
	private final RedisUtil redisUtil;
	private final JWTUtil jwtUtil;

	// 登录URL
	private final String LOGIN_URL;
	// token刷新的时间间隔
	private final Long REFRESH_TOKEN_TIME_INTERVAL;

	/**
	 * 构造函数
	 *
	 * @param sysParamUtil 系统参数工具
	 */
	public CurrentContextFilter(SysParamUtil sysParamUtil, UserPermissionUtil userPermissionUtil, RedisUtil redisUtil, JWTUtil jwtUtil) {
		this.sysParamUtil = sysParamUtil;
		this.userPermissionUtil = userPermissionUtil;
		this.redisUtil = redisUtil;
		this.jwtUtil = jwtUtil;
		// 从缓存中获取配置参数
		Map<String, String> sysItemMap = sysParamUtil.getSysParamGroupItemMap(SysParamGroupEnum.GUARD.toString()).getData();
		LOGIN_URL = sysParamUtil.mapGet(sysItemMap, "GUARD_LOGIN_URL");
		REFRESH_TOKEN_TIME_INTERVAL = Long.valueOf(sysParamUtil.mapGet(sysItemMap, "REFRESH_TOKEN_TIME_INTERVAL"));// TODO
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		LOGGER.debug("CurrentContextFilter init.");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		// 判断是否为登录请求，是则直接放行
		Boolean isLoginFlag = isLoginRequest(request);
		if (isLoginFlag) {
			chain.doFilter(req, resp);
		}
		// 初始化当前请求上下文
		CurrentContextHelper.initCurrentContext(null, request, LocalDateTime.now(), ApplicationInstanceManager.getApplicationInstanceId(), ApplicationInstanceManager.getServiceName());
		// 获取用户登录授权信息
		UserAuthInfo userAuthInfo = this.getUserAuthInfo(request, response);
		// 更新当前请求上下文
		CurrentContextHelper.updateCurrentContext(userAuthInfo, request, CurrentContextHelper.getRequestTime());
		// 放行
		chain.doFilter(req, new ReadableResponseWrapper(new HeaderWriterResponseWrapper((HttpServletResponse) resp)));
	}

	/**
	 * 判断是否为登录请求
	 *
	 * @param request 请求
	 * @return
	 */
	private Boolean isLoginRequest(HttpServletRequest request) {
		String url = request.getRequestURI().startsWith("/") ? request.getRequestURI() : "/" + request.getRequestURI();
		if (!LOGIN_URL.equals(url)) {
			return false;
		}
		return true;
	}

	@Override
	public void destroy() {
		LOGGER.debug("CurrentContextFilter destroy.");
	}

	/**
	 * 获取用户登录授权信息
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	private UserAuthInfo getUserAuthInfo(HttpServletRequest request, HttpServletResponse response) {
		// 获取token
		String token = this.getToken(request);
		if (StringUtils.isBlank(token)) {
			throw new IllegalStateException("Token not present.");
		}
		// 获取token过期时间(30day)，如果过期时间小于7day，则进行延长
		Date expireDate = jwtUtil.getExpireDate(token);
		// 获取当前最新时间戳
		Long currentTimeMillis = System.currentTimeMillis();
		String newToken = null;
		if (expireDate.getTime() - currentTimeMillis <= REFRESH_TOKEN_TIME_INTERVAL * 1000) {
			// 解析token为user
			User user = jwtUtil.parseToken(token);
			// 生成新的token
			newToken = jwtUtil.createToken(user, TokenTypeEnum.REFRESHTOKEN);
			// 将新的token缓存
			redisUtil.set(MvcStarterConstant.CACHE_KEY_TOKEN + user.getId(), newToken);
		}
		// Access-Control-Expose-Headers响应报头指示哪些报头可以公开为通过列出他们的名字的响应的一部分。
		response.setHeader("Access-Control-Expose-Headers", ApiConstant.ACCESSTOKEN + "," + ApiConstant.REFRESHTOKEN);
		// 是否是服务间内部请求
		boolean internalCall = this.isInternalCall(request);
		if (internalCall) {
			return StringUtils.isNotBlank(token) ? userPermissionUtil.getUserAuthInfo(token) : null;
		}
		// 是否是可以匿名访问的接口
		Boolean anonPermFlag = this.isAnonPermission(request);
		if (anonPermFlag) {
			return null;
		}
		UserAuthInfo userAuthInfo = userPermissionUtil.getUserAuthInfo(token);
		return userAuthInfo;
	}

	/**
	 * 是否是可以匿名访问的权限
	 *
	 * @param request 请求
	 * @return
	 */
	private Boolean isAnonPermission(HttpServletRequest request) {
		String uri = request.getRequestURI();
		String checkUrl = "/" + ApplicationInstanceManager.getApplicationName() + uri.replace(this.getTarget(request), "");
		return redisUtil.isSetMember(RbacStarterConstant.CACHE_KEY_PERMISSION_ANON, checkUrl);
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
	 * 获取token
	 *
	 * @param request 请求
	 * @return
	 */
	private String getToken(HttpServletRequest request) {
		String token = request.getHeader(CoreStarterConstant.TOKEN);
		if (StringUtils.isBlank(token) || "null".equalsIgnoreCase(token)) {
			token = request.getParameter(CoreStarterConstant.TOKEN);
			if (StringUtils.isBlank(token) || "null".equalsIgnoreCase(token)) {
				token = null;
			}
		}
		return token;
	}

	/**
	 * 获取target
	 *
	 * @param request 请求
	 * @return
	 */
	private String getTarget(HttpServletRequest request) {
		String target = request.getHeader("target");
		if (StringUtils.isBlank(target)) {
			target = request.getHeader("target");
		}
		return target == null ? "" : target;
	}

}
