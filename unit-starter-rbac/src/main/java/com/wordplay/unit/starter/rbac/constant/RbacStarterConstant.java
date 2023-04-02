package com.wordplay.unit.starter.rbac.constant;

/**
 * @author zhuangpf
 */
public class RbacStarterConstant {

	/**
	 * 双token动态刷新策略<br/>
	 * 缓存key：访问token，user:accesstoken:{id}
	 */
	public static final String CACHE_KEY_ACCESSTOKEN = "user:accesstoken:";

	/**
	 * 缓存key：刷新token，user:refreshtoken:{id}
	 */
	public static final String CACHE_KEY_REFRESHTOKEN = "user:refreshtoken:";

	/**
	 * 权限缓存key：role:permission:{id}
	 */
	public static final String CACHE_KEY_ROLE_PERMISSION = "role:permission:";

	/**
	 * 权限缓存key：user:verificationcode:{account的md5}
	 */
	public static final String CACHE_KEY_USER_VERIFICATIONCODE = "user:verificationcode:";

	/**
	 * 响应请求头，访问token
	 */
	public static final String ACCESSTOKEN = "accesstoken";

	/**
	 * 响应请求头，刷新token
	 */
	public static final String REFRESHTOKEN = "refreshtoken";

}
