package com.wordplay.unit.starter.shiro.util;

import com.alibaba.fastjson.JSON;
import com.wordplay.unit.starter.api.response.ResponseResult;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author zhuangpf
 */
public class ShiroUtil {

	/**
	 * 重新设置response响应信息
	 */
	public static void resetResponse(ServletResponse response, ResponseResult responseResult) {
		HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
		httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
		httpServletResponse.setCharacterEncoding("UTF-8");
		httpServletResponse.setContentType("application/json; charset=utf-8");
		try {
			PrintWriter out = httpServletResponse.getWriter();
			String resp = JSON.toJSONString(responseResult);
			out.append(resp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
