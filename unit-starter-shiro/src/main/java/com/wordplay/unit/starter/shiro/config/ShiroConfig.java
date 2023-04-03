package com.wordplay.unit.starter.shiro.config;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.wordplay.unit.starter.api.constant.ApiConstant;
import com.wordplay.unit.starter.cache.redis.util.RedisUtil;
import com.wordplay.unit.starter.shiro.constant.ShiroStarterConstant;
import com.wordplay.unit.starter.shiro.custom.JWTSubjectFactory;
import com.wordplay.unit.starter.shiro.filter.JWTShiroFilter;
import com.wordplay.unit.starter.shiro.model.ShiroRealm;
import com.wordplay.unit.starter.shiro.util.JWTUtil;
import com.wordplay.unit.starter.sysparam.service.impl.PlatformSysParamUtil;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.pam.AllSuccessfulStrategy;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * shiro 配置
 * https://blog.csdn.net/m0_52934670/article/details/121288133
 *
 * @author zhuangpf
 */
@Configuration
public class ShiroConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(ShiroConfig.class);

	// 登录URL
	private final String LOGIN_URL;
	// 登录成功跳转URL
	private final String SUCCESS_URL;
	// 认证失败跳转URL
	private final String UNAUTHORIZED_URL;
	// REMEMBER_ME最大时长(s)
	private final Integer REMEMBER_ME_COOKIE_MAX_AGE;
	// 过滤器链定义，JSON格式形如：{"/css/**":"anon"}
	private final String FILTER_CHAIN_DEFINITION;
	// cookie 加密密码
	private final String COOKIE_CIPHER_KEY;

	private RedisUtil redisUtil;
	private JWTUtil jwtUtil;
	private PlatformSysParamUtil platformSysParamUtil;

	public ShiroConfig(PlatformSysParamUtil platformSysParamUtil, RedisUtil redisUtil, JWTUtil jwtUtil) {
		this.platformSysParamUtil = platformSysParamUtil;
		this.redisUtil = redisUtil;
		this.jwtUtil = jwtUtil;
		// 从缓存中获取配置参数
		Map<String, String> sysItemMap = platformSysParamUtil.getSysParamGroupItemMap(ShiroStarterConstant.SYS_PARAM_GROUP).getData();
		LOGIN_URL = platformSysParamUtil.mapGet(sysItemMap, "LOGIN_URL");
		SUCCESS_URL = platformSysParamUtil.mapGet(sysItemMap, "SUCCESS_URL");
		UNAUTHORIZED_URL = platformSysParamUtil.mapGet(sysItemMap, "UNAUTHORIZED_URL");
		REMEMBER_ME_COOKIE_MAX_AGE = Integer.valueOf(platformSysParamUtil.mapGet(sysItemMap, "REMEMBER_ME_COOKIE_MAX_AGE"));
		FILTER_CHAIN_DEFINITION = platformSysParamUtil.mapGet(sysItemMap, "FILTER_CHAIN_DEFINITION");
		COOKIE_CIPHER_KEY = platformSysParamUtil.mapGet(sysItemMap, "COOKIE_CIPHER_KEY");
		LOGGER.info("SHIRO CONFIG : " + JSON.toJSONString(sysItemMap));
	}

	@Bean
	public static LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	/**
	 * 配置 realm
	 */
	@Bean
	public ShiroRealm shiroRealm() {
		ShiroRealm shiroRealm = new ShiroRealm();
		// 开启/关闭全局缓存管理，每次都去走ShiroRealm的认证方法
		shiroRealm.setCachingEnabled(false);
		return shiroRealm;
	}

	/**
	 * 多使用所有reaml一个通过就算认证通过作为策略
	 */
	@Bean
	public AuthenticationStrategy atLeastOneSuccessfulStrategy() {
		AtLeastOneSuccessfulStrategy atLeastOneSuccessfulStrategy = new AtLeastOneSuccessfulStrategy();
		return atLeastOneSuccessfulStrategy;
	}

	/**
	 * 多使用所有reaml均通过才算认证通过作为策略
	 */
	@Bean
	public AuthenticationStrategy allSuccessfulStrategy() {
		AllSuccessfulStrategy allSuccessfulStrategy = new AllSuccessfulStrategy();
		return allSuccessfulStrategy;
	}

	/**
	 * 多Realm认证策略
	 */
	@Bean
	public Authenticator authenticator() {
		ModularRealmAuthenticator modularRealmAuthenticator = new ModularRealmAuthenticator();
		// 使用一个通过验证即可通过验证的方式
		modularRealmAuthenticator.setAuthenticationStrategy(atLeastOneSuccessfulStrategy());
		return modularRealmAuthenticator;

	}

	/*@Bean(name = "lifecycleBeanPostProcessor")
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}*/

	/**
	 * 下面的代码是添加注解支持
	 */
	@Bean
//	@DependsOn("lifecycleBeanPostProcessor")
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
		// 强制使用cglib，防止重复代理和可能引起代理出错的问题，https://zhuanlan.zhihu.com/p/29161098
		defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
		return defaultAdvisorAutoProxyCreator;
	}

	@Bean
	public DefaultSessionStorageEvaluator defaultSessionStorageEvaluator() {
		DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
		defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
		return defaultSessionStorageEvaluator;
	}

	@Bean
	public DefaultSubjectDAO defaultSubjectDAO(DefaultSessionStorageEvaluator defaultSessionStorageEvaluator) {
		DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
		subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
		return subjectDAO;
	}

	@Bean
	public JWTSubjectFactory jwtSubjectFactory() {
		JWTSubjectFactory jwtSubjectFactory = new JWTSubjectFactory();
		return jwtSubjectFactory;
	}

	/**
	 * 配置 securityManager
	 */
	@Bean
	public SecurityManager securityManager(
			ShiroRealm shiroRealm,
//			EhCacheManager cacheManager,
			JWTSubjectFactory jwtSubjectFactory,
			DefaultSubjectDAO subjectDAO) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		// 多Reaml的认证策略
		securityManager.setAuthenticator(authenticator());
		// 设置多个realm授权验证时，只要一个通过即可
		List<Realm> realms = new ArrayList<>();
		realms.add(shiroRealm);
		// close session
		securityManager.setSubjectFactory(jwtSubjectFactory);
		securityManager.setSubjectDAO(subjectDAO);
	    /*// 关闭 ShiroDAO 功能
		DefaultSubjectDAO subjectDAO2 = new DefaultSubjectDAO();
		DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
		// 不需要将 Shiro Session 中的东西存到任何地方（包括 Http Session 中）
		defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
		subjectDAO2.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
		securityManager.setSubjectDAO(subjectDAO2);*/
		// 缓存
		/*securityManager.setCacheManager(cacheManager);*/
		// rememberMe
		securityManager.setRememberMeManager(rememberMeManager());
		// 设置单个realm
		securityManager.setRealm(shiroRealm);
		securityManager.setRealms(realms);
		return securityManager;
	}

	/**
	 * 添加自己的过滤器，自定义url规则
	 * Shiro自带拦截器配置规则
	 * rest：比如/admins/user/**=rest[user],根据请求的方法，相当于/admins/user/**=perms[user：method] ,其中method为post，get，delete等
	 * port：比如/admins/user/**=port[8081],当请求的url的端口不是8081是跳转到schemal：//serverName：8081?queryString,其中schmal是协议http或https等，serverName是你访问的host,8081是url配置里port的端口，queryString是你访问的url里的？后面的参数
	 * perms：比如/admins/user/**=perms[user：add：*],perms参数可以写多个，多个时必须加上引号，并且参数之间用逗号分割，比如/admins/user/**=perms["user：add：*,user：modify：*"]，当有多个参数时必须每个参数都通过才通过，想当于isPermitedAll()方法
	 * roles：比如/admins/user/**=roles[admin],参数可以写多个，多个时必须加上引号，并且参数之间用逗号分割，当有多个参数时，比如/admins/user/**=roles["admin,guest"],每个参数通过才算通过，相当于hasAllRoles()方法。//要实现or的效果看http://zgzty.blog.163.com/blog/static/83831226201302983358670/
	 * anon：比如/admins/**=anon 没有参数，表示可以匿名使用
	 * authc：比如/admins/user/**=authc表示需要认证才能使用，没有参数
	 * authcBasic：比如/admins/user/**=authcBasic没有参数表示httpBasic认证
	 * ssl：比如/admins/user/**=ssl没有参数，表示安全的url请求，协议为https
	 * user：比如/admins/user/**=user没有参数表示必须存在用户，当登入操作时不做检查
	 * 配置shiroFilterFactoryBean
	 * 配置基本的过滤规则
	 */
	@Bean
	public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		// 添加自己的过滤器取名为jwt
		Map<String, Filter> filterMap = new HashMap<>(16);
		filterMap.put("jwt", new JWTShiroFilter(platformSysParamUtil, redisUtil, jwtUtil));
		shiroFilterFactoryBean.setFilters(filterMap);
		// 配置管理器
		shiroFilterFactoryBean.setSecurityManager(securityManager);
		// 即跳转登录的页面
		shiroFilterFactoryBean.setLoginUrl(LOGIN_URL);
		shiroFilterFactoryBean.setSuccessUrl(SUCCESS_URL);
		shiroFilterFactoryBean.setUnauthorizedUrl(UNAUTHORIZED_URL);
		// 配置基本的策略规则,那些可以直接访问，哪些需要权限，按照顺序，先配置的起作用
		// 支持Ant风格
		Map<String, Object> configMap = JSON.parseObject(FILTER_CHAIN_DEFINITION);
		LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
		for (Map.Entry<String, Object> entry : configMap.entrySet()) {
			filterChainDefinitionMap.put(entry.getKey(), String.valueOf(entry.getValue()));
		}
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return shiroFilterFactoryBean;
	}

	/**
	 * 配置 rememberMeCookie
	 */
	public SimpleCookie rememberMeCookie() {
		// cookie名称remember-me
		SimpleCookie cookie = new SimpleCookie(ApiConstant.REMEMBER_ME);
		// cookie存活时间
		cookie.setMaxAge(REMEMBER_ME_COOKIE_MAX_AGE);
		// 请求是否为htt请求，防止恶意攻击
		cookie.setHttpOnly(true);
		return cookie;
	}

	/**
	 * 配置 CookieRememberMeManager
	 */
	public CookieRememberMeManager rememberMeManager() {
		CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
		cookieRememberMeManager.setCookie(rememberMeCookie());
		// cookie加密方式
		cookieRememberMeManager.setCipherKey(Base64.decode(COOKIE_CIPHER_KEY));
		return cookieRememberMeManager;
	}

	/**
	 * 配置 AuthorizationAttributeSourceAdvisor
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
		return authorizationAttributeSourceAdvisor;
	}

}
