package com.wordplay.unit.starter.mvc.filter;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.wordplay.unit.starter.api.response.ResponseResult;
import com.wordplay.unit.starter.cache.redis.util.RedisUtil;
import com.wordplay.unit.starter.mvc.constant.MvcStarterConstant;
import com.wordplay.unit.starter.mvc.util.JWTUtil;
import com.wordplay.unit.starter.rbac.constant.RbacStarterConstant;
import com.wordplay.unit.starter.rbac.entity.User;
import com.wordplay.unit.starter.rbac.model.TokenTypeEnum;
import com.wordplay.unit.starter.sysparam.model.SysParamGroupEnum;
import com.wordplay.unit.starter.sysparam.util.SysParamUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * token过滤器
 * 双token动态刷新策略
 *
 * @author zhuangpf
 * @date 2023-05-18
 */
public class TokenFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(TokenFilter.class);

	// refreshtoken刷新的时间间隔
	private final Long GUARD_REFRESH_TOKEN_TIME_INTERVAL;
	// 系统静态资源
	private final String STATIC_RESORUCES;
	private final List<String> staticResourceList;
	// 可匿名访问的资源
	private final String ANON_RESORUCES;
	private final List<String> anonResourceList;

	private RedisUtil redisUtil;
	private JWTUtil jwtUtil;
	// antPathMatcher路径匹配
	private AntPathMatcher antPathMatcher = new AntPathMatcher();

	/**
	 * 构造函数，初始化参数
	 */
	public TokenFilter(SysParamUtil sysParamUtil, RedisUtil redisUtil, JWTUtil jwtUtil) {
		this.redisUtil = redisUtil;
		this.jwtUtil = jwtUtil;
		Map<String, String> sysItemMap = sysParamUtil.getSysParamGroupItemMap(SysParamGroupEnum.GUARD.toString()).getData();
		GUARD_REFRESH_TOKEN_TIME_INTERVAL = Long.valueOf(sysParamUtil.mapGet(sysItemMap, "GUARD_REFRESH_TOKEN_TIME_INTERVAL"));
		STATIC_RESORUCES = sysParamUtil.mapGet(sysItemMap, MvcStarterConstant.STATIC_RESORUCES);
		String[] staticResourceArr = STATIC_RESORUCES.split(",");
		staticResourceList = new ArrayList<>(staticResourceArr.length);
		Collections.addAll(staticResourceList, staticResourceArr);
		ANON_RESORUCES = sysParamUtil.mapGet(sysItemMap, MvcStarterConstant.ANON_RESORUCES);
		String[] anonResourceArr = ANON_RESORUCES.split(",");
		anonResourceList = new ArrayList<>(anonResourceArr.length);
		Collections.addAll(anonResourceList, anonResourceArr);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		LOGGER.debug("TokenFilter init.");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// 是否是匿名访问资源：是否是可以访问的静态/匿名请求
		if (isPermissive(request)) {
			chain.doFilter(request, response);
		}
		boolean loggedIn = false;
		try {
			// 是否可以尝试进行认证
			if (this.isLoginAttempt(request, response)) {
				loggedIn = this.executeLogin(request, response);
			}
		} catch (Exception e) {
			// 认证出现错误，获取错误信息
			// 获取应用异常(该Cause是导致抛出此throwable(异常)的throwable(异常))
			if (e instanceof SignatureVerificationException) {
				// 该异常为JWT的AccessToken认证失败(Token或者密钥不正确)
				String msg = "token or cipher is not correct. (" + e.getMessage() + ")";
				throw new RuntimeException(msg);
			} else if (e instanceof TokenExpiredException) {
				// 该异常为JWT的AccessToken已过期，判断RefreshToken未过期就进行AccessToken刷新
				// 动态刷新双token
				this.doRefreshToken(request, response);
			} else {
				// 应用异常不为空
				if (null != e) {
					// 获取应用异常msg
					String msg = e.getMessage();
					throw new RuntimeException(msg);
				}
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		LOGGER.debug("TokenFilter destroy.");
	}

	/**
	 * 此时accesstoken已经过期<br/>
	 * 进行判断refreshtoken是否过期，未过期就返回新的accesstoken，(根据条件)刷新refreshtoken且继续正常访问
	 */
	private ResponseResult doRefreshToken(ServletRequest request, ServletResponse response) {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		// 获取token
		String accesstoken = httpRequest.getHeader(MvcStarterConstant.ACCESSTOKEN);
		String refreshtoken = httpRequest.getHeader(MvcStarterConstant.REFRESHTOKEN);
		// 获取用户ID
		String idStr = jwtUtil.getClaim(refreshtoken, "id");
		// 缓存中的refreshToken
		String refreshTokenCache = (String) redisUtil.get(RbacStarterConstant.CACHE_KEY_REFRESHTOKEN + idStr);
		// accessToken & refreshtoken 都已经过期了
		if (StringUtils.isBlank(refreshTokenCache)) {
			return ResponseResult.fail("token has expired");
		}
		// 校验refreshtoken是否和缓存中一致
		if (!refreshtoken.equals(refreshTokenCache)) {
			return ResponseResult.fail("invalid refreshtoken");
		}
		// 获取refreshtoken的过期时间
		Date expireDate = jwtUtil.getExpireDate(refreshtoken);
		// 获取当前最新时间戳
		Long currentTimeMillis = System.currentTimeMillis();
		// refreshtoken 过期时间减去当前时间小于等于刷新时间，则刷新 refreshtoken
		User user = jwtUtil.parseToken(refreshtoken);
		String refreshtokenNew = null;
		if (expireDate.getTime() - currentTimeMillis <= GUARD_REFRESH_TOKEN_TIME_INTERVAL * 1000) {
			// 生成新的refreshtoken
			refreshtokenNew = jwtUtil.createToken(user, TokenTypeEnum.REFRESHTOKEN);
			redisUtil.set(RbacStarterConstant.CACHE_KEY_REFRESHTOKEN + idStr, refreshtokenNew);
		}
		// 生成新的accesstoken
		String accesstokenNew = jwtUtil.createToken(user, TokenTypeEnum.ACCESSTOKEN);
		redisUtil.set(RbacStarterConstant.CACHE_KEY_ACCESSTOKEN + idStr, accesstokenNew);
		// TODO 重新再次进行登录


		// 最后将刷新的AccessToken存放在Response的Header中的Authorization字段返回
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.setHeader(MvcStarterConstant.ACCESSTOKEN, accesstokenNew);
		if (StringUtils.isNotEmpty(refreshtokenNew)) {
			httpServletResponse.setHeader(MvcStarterConstant.REFRESHTOKEN, refreshTokenCache);
		}
		// Access-Control-Expose-Headers响应报头指示哪些报头可以公开为通过列出他们的名字的响应的一部分。
		httpServletResponse.setHeader("Access-Control-Expose-Headers", MvcStarterConstant.ACCESSTOKEN + "," + MvcStarterConstant.REFRESHTOKEN);
		return ResponseResult.success();
	}

	/**
	 * 进行用户进行登录认证授权
	 */
	private boolean executeLogin(ServletRequest request, ServletResponse response) {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String accesstoken = httpRequest.getHeader(MvcStarterConstant.ACCESSTOKEN);
		// token解析成用户
		User user = jwtUtil.parseToken(accesstoken);
		String accesstokenCache = (String) redisUtil.get(RbacStarterConstant.CACHE_KEY_ACCESSTOKEN + user.getId());
		if (!accesstoken.equals(accesstokenCache)) {
			throw new RuntimeException("invalid accesstoken");
		}
		// 将用户信息存入到当前上下文环境中，便于在后续的业务代码中取出相关的信息
		// TODO

		return true;
	}

	/**
	 * 是否是可以访问的静态/匿名请求
	 * getRequestURI:/test/test.jsp
	 * getRequestURL:http://localhost:8080/test/test.jsp
	 */
	protected boolean isPermissive(ServletRequest request) {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String requestURI = httpRequest.getRequestURI();
		// 是否是静态资源/匿名资源
		return isStaticResource(requestURI) && isAnonResource(requestURI);
	}

	/**
	 * 是否是可以匿名访问的资源
	 */
	private boolean isAnonResource(String requestURI) {
		for (String pattern : anonResourceList) {
			boolean match = antPathMatcher.match(pattern, requestURI);
			if (match) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否是请求静态资源
	 */
	private boolean isStaticResource(String requestURI) {
		for (String suffix : staticResourceList) {
			if (requestURI.endsWith(suffix)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否是可尝试登录的请求
	 */
	protected final boolean isLoginRequest(ServletRequest request, ServletResponse response) {
		return this.isLoginAttempt(request, response);
	}

	/**
	 * 当前的请求是否可以尝试进行登录授权<br/>
	 * 检测header里面是否包含accesstoken、refreshtoken字段，有则进行Token登录认证授权
	 */
	protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
		// 需要是BASIC开头的token
		/*return super.isLoginAttempt(request, response);*/
		// 拿到当前Header中Authorization的AccessToken(Shiro中getAuthzHeader方法已经实现)
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String accessToken = httpRequest.getHeader(MvcStarterConstant.ACCESSTOKEN);
		String refreshToken = httpRequest.getHeader(MvcStarterConstant.REFRESHTOKEN);
		return StringUtils.isNotBlank(accessToken) && StringUtils.isNotBlank(refreshToken);
	}

}
