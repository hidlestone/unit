package com.wordplay.unit.starter.mvc.advice;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author zhuangpf
 * @date 2023-05-18
 */
@ControllerAdvice
public class StringRequestBodyAdvice implements RequestBodyAdvice {

	@Override
	public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
		return String.class.equals(type);
	}

	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
		return httpInputMessage;
	}

	@Override
	public Object afterBodyRead(Object body, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
		if (body != null && body instanceof String) {
			String bodyStr = body.toString();
			if (bodyStr.startsWith("\"") && bodyStr.endsWith("\"")) {
				return StringEscapeUtils.unescapeJava(bodyStr.substring(1, bodyStr.length() - 1));
			}
		}
		return body;
	}

	@Override
	public Object handleEmptyBody(Object body, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
		return body;
	}
}
