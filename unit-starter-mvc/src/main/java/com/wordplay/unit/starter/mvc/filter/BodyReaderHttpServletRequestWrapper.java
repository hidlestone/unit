package com.wordplay.unit.starter.mvc.filter;

import com.wordplay.unit.starter.core.context.CoreStarterConstant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * 装饰者模式，对请求对象 request 中的参数进行处理
 *
 * @author zhuangpf
 */
public class BodyReaderHttpServletRequestWrapper extends HttpServletRequestWrapper {

	private final byte[] body;
	private String bodyStr;

	public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
		// 获取请求的编码
		String characterEncoding = request.getCharacterEncoding();
		if (null == characterEncoding) {
			characterEncoding = CoreStarterConstant.DEFAULT_CHARSET;
		}
		// 根据 UTF-8 读取请求体的内容
		String bodyString = this.getBodyString(request, characterEncoding);
		this.body = bodyString.getBytes(Charset.forName(characterEncoding));
		this.bodyStr = bodyString;
	}

	public String getBodyString(HttpServletRequest request, String charset) {
		StringBuilder sb = new StringBuilder();
		InputStream inputStream = null;
		BufferedReader reader = null;
		try {
			inputStream = request.getInputStream();
			reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(charset)));
			char[] bodyCharBuffer = new char[1024];
			int len;
			while ((len = reader.read(bodyCharBuffer)) != -1) {
				sb.append(new String(bodyCharBuffer, 0, len));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		return sb.toString();
	}
}
