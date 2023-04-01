package com.wordplay.unit.starter.data.mp;

import java.util.Map;

/**
 * 本地线程用户信息
 * <p>
 * userId、userName
 *
 * @author zhuangpf
 */
public class UserThreadLocal {

	private static final ThreadLocal<Map<String, String>> LOCAL = new ThreadLocal<>();

	public static void put(Map<String, String> userMap) {
		LOCAL.set(userMap);
	}

	public static Map<String, String> get() {
		return LOCAL.get();
	}

	public static void remove() {
		LOCAL.remove();
	}

}
