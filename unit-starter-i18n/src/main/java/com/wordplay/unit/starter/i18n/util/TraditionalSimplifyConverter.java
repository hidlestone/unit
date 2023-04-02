package com.wordplay.unit.starter.i18n.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ResourceBundle;

/**
 * 繁体简体转换器
 *
 * @author zhuangpf
 */
public class TraditionalSimplifyConverter {

	// 简体转换器
	private static final TraditionalSimplifyConverter simplifyConverter = new TraditionalSimplifyConverter();
	// 繁体转换器
	private static final TraditionalSimplifyConverter traditionalConverter = new TraditionalSimplifyConverter();

	private ResourceBundle resourceBundle = null;

	public static TraditionalSimplifyConverter getInstance(TargetEnum targetEnum) {
		// 简体->繁体转换器
		if (targetEnum.equals(TargetEnum.TRADITIONAL)) {
			traditionalConverter.resourceBundle = ResourceBundle.getBundle("simplify2Traditional");
			return traditionalConverter;
		} else {
			// 繁体->简体转换器
			simplifyConverter.resourceBundle = ResourceBundle.getBundle("traditional2Simplify");
			return simplifyConverter;
		}
	}

	public static String convert(String str, TargetEnum targetEnum) {
		return getInstance(targetEnum).convert(str);
	}

	public String convert(String str) {
		if (StringUtils.isBlank(str)) {
			throw new IllegalArgumentException("string is blank.");
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			if (str.length() > 1 && this.resourceBundle.containsKey(str)) {
				return this.resourceBundle.getString(str);
			} else {
				char[] charArray = str.toCharArray();
				int length = charArray.length;
				for (int i = 0; i < length; ++i) {
					char ch = charArray[i];
					// 单字
					String chStr = String.valueOf(ch);
					stringBuilder.append(this.resourceBundle.containsKey(chStr)
							? this.resourceBundle.getString(str) : chStr);
				}
				return stringBuilder.toString();
			}
		}
	}

	/**
	 * 目标转换语言
	 */
	public static enum TargetEnum {
		TRADITIONAL,
		SIMPLIFY;

		TargetEnum() {
		}
	}
}
