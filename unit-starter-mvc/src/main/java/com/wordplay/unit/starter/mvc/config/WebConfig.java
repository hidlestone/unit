package com.wordplay.unit.starter.mvc.config;

import com.wordplay.unit.starter.cache.redis.util.RedisUtil;
import com.wordplay.unit.starter.mvc.filter.CurrentContextFilter;
import com.wordplay.unit.starter.mvc.filter.RepeatSubmitFilter;
import com.wordplay.unit.starter.mvc.util.JWTUtil;
import com.wordplay.unit.starter.mvc.util.UserPermissionUtil;
import com.wordplay.unit.starter.sysparam.util.SysParamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * @author zhuangpf
 * @date 2023-05-18
 */
@Configuration
@Import({FeignClientsConfiguration.class})
@Order(2147483647)
public class WebConfig implements WebMvcConfigurer {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebConfig.class);

	// 过滤器顺序
	public static final Integer LANGUAGE_FILTER_ORDER = -2147483548;
	public static final Integer CURRENT_CONTEXT_FILTER_ORDER = 2147483448;
	public static final Integer SIGNATURE_FILTER_ORDER = -2147483348;
	public static final Integer XSS_FILTER_ORDER = -2147483248;

	@Autowired(required = false)
	private static SysParamUtil sysParamUtil;
	@Autowired(required = false)
	private UserPermissionUtil userPermissionUtil;
	@Autowired(required = false)
	private RedisUtil redisUtil;
	@Autowired(required = false)
	private JWTUtil jwtUtil;

	/**
	 * 异步请求(Async-Support)配置 ASYNC_SUPPORT_CONFIG
	 */
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		// 设置默认的超时时间，（毫秒，Tomcat下默认是10000毫秒，即10秒）
		configurer.setDefaultTimeout(60000L); // 60s
		// 注册异步的拦截器
		configurer.registerCallableInterceptors(new CallableProcessingInterceptor[]{this.timeoutInterceptor()});
		// 设定异步请求线程池callable等, spring默认线程不可重用
		configurer.setTaskExecutor(this.threadPoolTaskExecutor());
	}

	/**
	 * 异步请求(Async-Support)线程池
	 */
	@Bean
	@Primary
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor t = new ThreadPoolTaskExecutor();
		t.setCorePoolSize(5);
		t.setMaxPoolSize(20);
		t.setQueueCapacity(100);
		t.setThreadNamePrefix("Async-Support-Thread-");
		return t;
	}

	/**
	 * 异步请求超时拦截器
	 */
	@Bean
	public TimeoutCallableProcessingInterceptor timeoutInterceptor() {
		return new TimeoutCallableProcessingInterceptor();
	}

	/**
	 * 跨域资源共享（Cross-origin resource sharing）<br>
	 * 设置跨域请求参数<br>
	 * Access-Control-Allow-Credentials：该响应头非必须，值是bool类型，表示是否允许发送Cookie。<br>
	 * Access-Control-Allow-Origin：该响应头是服务器必须返回的。它的值要么是请求时Origin的值（可从request里获取），要么是*这样浏览器才会接受服务器的返回结果。<br>
	 * Access-Control-Allow-Headers：表示我服务器支持的所有头字段，不限于预检请求中的头字段。<br>
	 * Access-Control-Max-Age：表示需要缓存预检结果多长时间，单位是秒。<br>
	 */
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
		corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
		corsConfiguration.setAllowedMethods(Arrays.asList("*"));
		source.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(source);
	}

	/**
	 * 注册拦截器
	 */
	/*@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 公共拦截器
		registry.addInterceptor(new CommonInfoInterceptor()).addPathPatterns(new String[]{"/**"});
		// 证书校验拦截器
		// registry.addInterceptor(new LicenseInterceptor()).addPathPatterns(new String[]{"/**"});
		// token 拦截器
		registry.addInterceptor(new TokenInterceptor()).addPathPatterns(new String[]{"/**"});
	}*/

	/**
	 * Filter：当前请求上下文过滤器
	 */
	@Bean
	public FilterRegistrationBean<CurrentContextFilter> currentContextFilter() {
		FilterRegistrationBean<CurrentContextFilter> bean = new FilterRegistrationBean();
		bean.setFilter(new CurrentContextFilter(sysParamUtil, userPermissionUtil, redisUtil, jwtUtil));
		bean.addUrlPatterns(new String[]{"/*"});
		bean.setOrder(CURRENT_CONTEXT_FILTER_ORDER);
		return bean;
	}

	/**
	 * Filter：防止表单重复提交的过滤器
	 */
	@Bean
	public FilterRegistrationBean<RepeatSubmitFilter> repeatSignatureValidateFilter() {
		FilterRegistrationBean<RepeatSubmitFilter> bean = new FilterRegistrationBean();
		bean.setFilter(new RepeatSubmitFilter(sysParamUtil));
		bean.addUrlPatterns(new String[]{"/*"});
		bean.setOrder(SIGNATURE_FILTER_ORDER);
		return bean;
	}

}
