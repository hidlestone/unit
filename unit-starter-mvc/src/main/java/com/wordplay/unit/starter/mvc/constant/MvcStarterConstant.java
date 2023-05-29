package com.wordplay.unit.starter.mvc.constant;

/**
 * @author zhuangpf
 * @date 2023-05-18
 */
public class MvcStarterConstant {

	// 签名
	public final static String SIGN = "sign";
	// 加密请求体
	public final static String ENCRYPT_CODE = "encrypt-code";
	// nonce(Number once)校验
	public final static String NONCE = "nonce";
	// 请求时间
	public final static String STIME = "stime";
	
	public final static String XSS_ENABLE = "XSS_ENABLE";
	public final static String EXCLUDE_PATTERN = "EXCLUDE_PATTERN";
	public final static String INCLUDE_PATTERN = "INCLUDE_PATTERN";

	public final static String DEFAULT_ISSUER = "DEFAULT_ISSUER";
	public final static String JWT_SECRET_KEY = "JWT_SECRET_KEY";
	public final static String SHIRO_REFRESHTOKEN_TTL = "SHIRO_REFRESHTOKEN_TTL";
	public final static String SHIRO_ACCESSTOKEN_TTL = "SHIRO_ACCESSTOKEN_TTL";

	public final static String ID = "id";
	public final static String ACCOUNT = "account";
	public final static String USERNAME = "username";

	public final static String STATIC_RESORUCES = "STATIC_RESORUCES";
	public final static String ANON_RESORUCES = "ANON_RESORUCES";

	public final static String ACCESSTOKEN = "accesstoken";
	public final static String REFRESHTOKEN = "refreshtoken";

	public final static String APPLICATION_SIGNATURE_REPEAT_CHECK_ENABLE = "APPLICATION_SIGNATURE_REPEAT_CHECK_ENABLE";
	public final static String APPLICATION_SIGNATURE_ENABLE = "APPLICATION_SIGNATURE_ENABLE";
	public final static String APPLICATION_SIGNATURE_EXCLUDE_SERVICE_NAMES = "APPLICATION_SIGNATURE_EXCLUDE_SERVICE_NAMES";
	public final static String APPLICATION_SIGNATURE_CONTENT_TYPES = "APPLICATION_SIGNATURE_CONTENT_TYPES";
	public final static String APPLICATION_SIGNATURE_EXCLUDE_URLS = "APPLICATION_SIGNATURE_EXCLUDE_URLS";
	public final static String APPLICATION_SIGNATURE_METHOD_NAME = "APPLICATION_SIGNATURE_METHOD_NAME";
	public final static String APPLICATION_SIGNATURE_STIME_TIMEOUT = "APPLICATION_SIGNATURE_STIME_TIMEOUT";
	public final static String APPLICATION_DEFAULT_STIME_TIMEOUT = "APPLICATION_DEFAULT_STIME_TIMEOUT";
	public final static String APPLICATION_SIGNATURE_NONCE_TIMEOUT = "APPLICATION_SIGNATURE_NONCE_TIMEOUT";
	public final static String APPLICATION_SIGNATURE_EXCLUDE_PARAM_NAMES = "APPLICATION_SIGNATURE_EXCLUDE_PARAM_NAMES";
	public final static String DEFAULT_NEED_CHECK_METHOD_NAMES = "DEFAULT_NEED_CHECK_METHOD_NAMES";
	public final static String DEFAULT_EXCLUDE_PARAM_NAMES = "DEFAULT_EXCLUDE_PARAM_NAMES";
	public final static String RSA_PRIVATE_KEY = "RSA_PRIVATE_KEY";

	// 缓存key：sign:nonce:
	public static final String SIGN_NONCE = "sign:nonce:";
	// nonce默认超时时间60(s)
	public static final Integer INIT_NONCE_TIMEOUT = 60000;

	// 缓存key：访问token，user:token:{id}
	public static final String CACHE_KEY_TOKEN = "user:token:";
}
