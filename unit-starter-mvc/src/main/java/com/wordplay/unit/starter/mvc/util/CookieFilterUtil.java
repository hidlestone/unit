package com.wordplay.unit.starter.mvc.util;

import java.util.regex.Pattern;

/**
 * cookie过滤器工具
 *
 * @author zhuangpf
 */
public class CookieFilterUtil {

	public static final Pattern SCRIPT_PATTERN = Pattern.compile("<script>(.*?)</script>", 2);
	public static final Pattern SRC_PATTERN = Pattern.compile("src[\r\n]*=[\r\n]*\\'(.*?)\\'", 42);
	public static final Pattern HREF_PATTERN = Pattern.compile("href[\r\n]*=[\r\n]*\\\"(.*?)\\\"", 42);
	public static final Pattern SCRIPT_PATTERN2 = Pattern.compile("</script>", 2);
	public static final Pattern SCRIPT_PATTERN3 = Pattern.compile("<script(.*?)>", 42);
	public static final Pattern EVAL_PATTERN = Pattern.compile("eval\\((.*?)\\)", 42);
	public static final Pattern EXPRESSION_PATTERN = Pattern.compile("expression\\((.*?)\\)", 42);
	public static final Pattern JAVASCRIPT_PATTERN = Pattern.compile("javascript:", 2);
	public static final Pattern VBSCRIPT_PATTERN = Pattern.compile("vbscript:", 2);
	public static final Pattern ONLOAD_PATTERN = Pattern.compile("onload(.*?)=", 42);

	/**
	 * 清除xss脚本
	 */
	public static String xssClean(String value) {
		if (value != null) {
			value = value.replaceAll("\u0000", "");
			value = SCRIPT_PATTERN.matcher(value).replaceAll("");
			value = SRC_PATTERN.matcher(value).replaceAll("");
			value = HREF_PATTERN.matcher(value).replaceAll("");
			value = SCRIPT_PATTERN2.matcher(value).replaceAll("");
			value = SCRIPT_PATTERN3.matcher(value).replaceAll("");
			value = EVAL_PATTERN.matcher(value).replaceAll("");
			value = EXPRESSION_PATTERN.matcher(value).replaceAll("");
			value = JAVASCRIPT_PATTERN.matcher(value).replaceAll("");
			value = VBSCRIPT_PATTERN.matcher(value).replaceAll("");
			value = ONLOAD_PATTERN.matcher(value).replaceAll("");
		}
		return value;
	}

	/**
	 * 校验是否存在xss脚本
	 */
	public static boolean isValidate(String value) {
		boolean flag = false;
		if (value != null) {
			value = value.replaceAll("\u0000", "");
			if (SCRIPT_PATTERN.matcher(value).find()) {
				flag = true;
				return flag;
			}
			if (SRC_PATTERN.matcher(value).find()) {
				flag = true;
				return flag;
			}
			if (HREF_PATTERN.matcher(value).find()) {
				flag = true;
				return flag;
			}
			if (SCRIPT_PATTERN2.matcher(value).find()) {
				flag = true;
				return flag;
			}
			if (SCRIPT_PATTERN3.matcher(value).find()) {
				flag = true;
				return flag;
			}
			if (EVAL_PATTERN.matcher(value).find()) {
				flag = true;
				return flag;
			}
			if (EXPRESSION_PATTERN.matcher(value).find()) {
				flag = true;
				return flag;
			}
			if (JAVASCRIPT_PATTERN.matcher(value).find()) {
				flag = true;
				return flag;
			}
			if (VBSCRIPT_PATTERN.matcher(value).find()) {
				flag = true;
				return flag;
			}
			if (ONLOAD_PATTERN.matcher(value).find()) {
				flag = true;
				return flag;
			}
		}
		return flag;
	}
}
