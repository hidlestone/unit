package com.wordplay.unit.starter.control.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 使用feign进行调用的时候，需要往下游的服务携带请求头<br/>
 * https://www.jianshu.com/p/6cbf91572c21
 * http://www.manongjc.com/detail/14-kwcgxhesqshssdg.html
 *
 * @author zhuangpf
 */
@Configuration
public class FeignRequestConfig implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		Enumeration<String> headerNames = request.getHeaderNames();
		if (headerNames != null) {
			while (headerNames.hasMoreElements()) {
				String name = headerNames.nextElement();
				String values = request.getHeader(name);
				// 跳过 content-length，解决too many bites written的问题
				if (name.equalsIgnoreCase("content-length")) {
					continue;
				}
				template.header(name, values);
			}
		}
	}

}
