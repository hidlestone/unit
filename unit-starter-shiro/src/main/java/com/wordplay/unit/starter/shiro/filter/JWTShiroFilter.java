package com.wordplay.unit.starter.shiro.filter;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.wordplay.unit.starter.api.constant.ApiConstant;
import com.wordplay.unit.starter.api.response.ResponseResult;
import com.wordplay.unit.starter.cache.redis.util.RedisUtil;
import com.wordplay.unit.starter.rbac.constant.RbacStarterConstant;
import com.wordplay.unit.starter.rbac.entity.User;
import com.wordplay.unit.starter.rbac.model.TokenTypeEnum;
import com.wordplay.unit.starter.shiro.custom.JWTToken;
import com.wordplay.unit.starter.shiro.util.JWTUtil;
import com.wordplay.unit.starter.shiro.util.ShiroUtil;
import com.wordplay.unit.starter.sysparam.model.SysParamGroupEnum;
import com.wordplay.unit.starter.sysparam.util.SysParamUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * jwt过滤器，实现双token的动态刷新<br/>
 * 登录/登出/匿名的URL不进行拦截
 *
 * @author zhuangpf
 */
public class JWTShiroFilter extends BasicHttpAuthenticationFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(JWTShiroFilter.class);

	/**
	 * refreshtoken刷新的时间间隔
	 */
	private final Long SHIRO_REFRESH_TOKEN_TIME_INTERVAL;

	private RedisUtil redisUtil;
	private JWTUtil jwtUtil;

	/**
	 * 构造函数
	 */
	public JWTShiroFilter(SysParamUtil sysParamUtil, RedisUtil redisUtil, JWTUtil jwtUtil) {
		this.redisUtil = redisUtil;
		this.jwtUtil = jwtUtil;
		Map<String, String> sysItemMap = sysParamUtil.getSysParamGroupItemMap(SysParamGroupEnum.SHIRO.toString()).getData();
		SHIRO_REFRESH_TOKEN_TIME_INTERVAL = Long.valueOf(sysParamUtil.mapGet(sysItemMap, "SHIRO_REFRESH_TOKEN_TIME_INTERVAL"));
	}

	/**
	 * 是否允许访问
	 * 1、校验accesstoken和refreshtoken的请求头是否存在。且不是登录的url或者可匿名访问，返回false。
	 */
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		HttpServletRequest httpRequest = WebUtils.toHttp(request);
		String httpMethod = httpRequest.getMethod();
		Set<String> methods = this.httpMethodsFromOptions(((String[]) mappedValue));
		boolean authcRequired = methods.size() == 0;
		for (String option : methods) {
			if (httpMethod.toUpperCase(Locale.ENGLISH).equals(option)) {
				authcRequired = true;
				break;
			}
		}
		return authcRequired ? (!this.isLoginRequest(request, response) && this.isPermissive(mappedValue)) : true;
	}

	private Set<String> httpMethodsFromOptions(String[] options) {
		Set<String> methods = new HashSet();
		if (options != null) {
			String[] optionArr = options;
			int len = options.length;
			for (int i = 0; i < len; ++i) {
				String option = optionArr[i];
				if (!option.equalsIgnoreCase("permissive")) {
					methods.add(option.toUpperCase(Locale.ENGLISH));
				}
			}
		}
		return methods;
	}

	/**
	 * isAccessAllowed 返回false，进入此方法。
	 */
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		boolean loggedIn = false;
		try {
			// 是否可以尝试进行认证
			if (this.isLoginAttempt(request, response)) {
				loggedIn = this.executeLogin(request, response);
			}
		} catch (Exception e) {
			// 认证出现错误，获取错误信息
			ResponseResult responseResult = ResponseResult.success();
			// 获取应用异常(该Cause是导致抛出此throwable(异常)的throwable(异常))
			if (e instanceof SignatureVerificationException) {
				// 该异常为JWT的AccessToken认证失败(Token或者密钥不正确)
				String msg = "token or cipher is not correct. (" + e.getMessage() + ")";
				responseResult = ResponseResult.fail(msg);
			} else if (e instanceof TokenExpiredException) {
				// 该异常为JWT的AccessToken已过期，判断RefreshToken未过期就进行AccessToken刷新
				// 动态刷新双token
				responseResult = this.doRefreshToken(request, response);
				if (responseResult.isSuccess()) {
					return true;
				}
			} else {
				// 应用异常不为空
				if (null != e) {
					// 获取应用异常msg
					String msg = e.getMessage();
					responseResult = ResponseResult.fail(msg);
				}
			}
			ShiroUtil.resetResponse(response, responseResult);
		}
		return loggedIn;
	}

	/**
	 * 此时accesstoken已经过期<br/>
	 * 进行判断refreshtoken是否过期，未过期就返回新的accesstoken，(根据条件)刷新refreshtoken且继续正常访问
	 */
	private ResponseResult doRefreshToken(ServletRequest request, ServletResponse response) {
		HttpServletRequest httpRequest = WebUtils.toHttp(request);
		String accesstoken = httpRequest.getHeader(ApiConstant.ACCESSTOKEN);
		String refreshtoken = httpRequest.getHeader(ApiConstant.REFRESHTOKEN);
		/*ResponseResult responseResult = jwtUtil.accessRefreshTokeValidate(accesstoken, refreshtoken);
		if (!responseResult.isSuccess()) {
			return responseResult;
		}*/
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
		if (expireDate.getTime() - currentTimeMillis <= SHIRO_REFRESH_TOKEN_TIME_INTERVAL * 1000) {
			// 生成新的refreshtoken
			refreshtokenNew = jwtUtil.createToken(user, TokenTypeEnum.REFRESHTOKEN);
			redisUtil.set(RbacStarterConstant.CACHE_KEY_REFRESHTOKEN + idStr, refreshtokenNew);
		}
		// 生成新的accesstoken
		String accesstokenNew = jwtUtil.createToken(user, TokenTypeEnum.ACCESSTOKEN);
		redisUtil.set(RbacStarterConstant.CACHE_KEY_ACCESSTOKEN + idStr, accesstokenNew);
		// 提交给UserRealm进行认证，如果错误他会抛出异常并被捕获，如果没有抛出异常则代表登入成功，返回true
		JWTToken jwtToken = new JWTToken(user.getUsername(), accesstokenNew);
		// 重新在进行登录
		this.getSubject(request, response).login(jwtToken);
		// 最后将刷新的AccessToken存放在Response的Header中的Authorization字段返回
		HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
		httpServletResponse.setHeader(ApiConstant.ACCESSTOKEN, accesstokenNew);
		if (StringUtils.isNotEmpty(refreshtokenNew)) {
			httpServletResponse.setHeader(ApiConstant.REFRESHTOKEN, refreshTokenCache);
		}
		// Access-Control-Expose-Headers响应报头指示哪些报头可以公开为通过列出他们的名字的响应的一部分。
		httpServletResponse.setHeader("Access-Control-Expose-Headers", ApiConstant.ACCESSTOKEN + "," + ApiConstant.REFRESHTOKEN);
		return ResponseResult.success();
	}

	/**
	 * 进行用户进行登录认证授权
	 */
	@Override
	protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpRequest = WebUtils.toHttp(request);
		String accessToken = httpRequest.getHeader(ApiConstant.ACCESSTOKEN);
		String username = jwtUtil.getClaim(accessToken, "username");
		JWTToken jwtToken = new JWTToken(username, accessToken);
		// 提交给UserRealm进行认证，如果错误会抛出异常并被捕获
//		this.getSubject(request, response).login(jwtToken); // 这个方法会执行验证和授权
		Subject currentUser = SecurityUtils.getSubject();
		currentUser.login(jwtToken);
		// 如果没有抛出异常则代表登入成功，返回true
		return true;
	}

	/**
	 * 当前的请求是否可以尝试进行登录授权<br/>
	 * 检测header里面是否包含accesstoken、refreshtoken字段，有则进行Token登录认证授权
	 */
	@Override
	protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
		// 需要是BASIC开头的token
		/*return super.isLoginAttempt(request, response);*/
		// 拿到当前Header中Authorization的AccessToken(Shiro中getAuthzHeader方法已经实现)
		HttpServletRequest httpRequest = WebUtils.toHttp(request);
		String accessToken = httpRequest.getHeader(ApiConstant.ACCESSTOKEN);
		String refreshToken = httpRequest.getHeader(ApiConstant.REFRESHTOKEN);
		return !StringUtils.isBlank(accessToken) && !StringUtils.isBlank(refreshToken);
	}

}
