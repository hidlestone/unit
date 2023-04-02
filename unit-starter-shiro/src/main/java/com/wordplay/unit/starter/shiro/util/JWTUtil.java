package com.wordplay.unit.starter.shiro.util;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.wordplay.unit.starter.api.response.ResponseResult;
import com.wordplay.unit.starter.rbac.entity.User;
import com.wordplay.unit.starter.rbac.model.TokenTypeEnum;
import com.wordplay.unit.starter.sysparam.model.SysParamGroupEnum;
import com.wordplay.unit.starter.sysparam.service.impl.PlatformSysParamUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author zhuangpf
 */
@Component
public class JWTUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(JWTUtil.class);

	/**
	 * 默认token发布者
	 */
	private final String DEFAULT_ISSUER;
	/**
	 * JWT加密key
	 */
	private final String JWT_SECRET_KEY;
	/**
	 * refreshtoken过期时间
	 */
	private final Long SHIRO_REFRESHTOKEN_TTL;
	/**
	 * accesstoken过期时间
	 */
	private final Long SHIRO_ACCESSTOKEN_TTL;

	public JWTUtil(PlatformSysParamUtil platformSysParamUtil) {
		// 从缓存中获取配置参数：JWT
		Map<String, String> sysItemMap = platformSysParamUtil.getSysParamGroupItemMap(SysParamGroupEnum.JWT.toString()).getData();
		DEFAULT_ISSUER = platformSysParamUtil.mapGet(sysItemMap, "DEFAULT_ISSUER");
		JWT_SECRET_KEY = platformSysParamUtil.mapGet(sysItemMap, "JWT_SECRET_KEY");
		sysItemMap = platformSysParamUtil.getSysParamGroupItemMap(SysParamGroupEnum.SHIRO.toString()).getData();
		SHIRO_REFRESHTOKEN_TTL = Long.valueOf(platformSysParamUtil.mapGet(sysItemMap, "SHIRO_REFRESHTOKEN_TTL"));
		SHIRO_ACCESSTOKEN_TTL = Long.valueOf(platformSysParamUtil.mapGet(sysItemMap, "SHIRO_ACCESSTOKEN_TTL"));
		LOGGER.info("JWT CONFIG : " + JSON.toJSONString(sysItemMap));
	}

	/**
	 * 为用户构建token
	 * 只包含：id、account、、username 三个字段
	 *
	 * @param user 登录用户
	 * @return token
	 */
	public String createToken(User user, TokenTypeEnum tokenTypeEnum) {
		Long JWT_TTL;
		if (TokenTypeEnum.ACCESSTOKEN.equals(tokenTypeEnum)) {
			JWT_TTL = SHIRO_ACCESSTOKEN_TTL;
		} else {
			JWT_TTL = SHIRO_REFRESHTOKEN_TTL;
		}
		// 使用HMAC256算法
		Algorithm algo = Algorithm.HMAC256(JWT_SECRET_KEY);
		// 过期时间
		Date expireDate = new Date(System.currentTimeMillis() + JWT_TTL * 1000);
		String token = JWT.create().withIssuer(DEFAULT_ISSUER)
				// 构建claim信息
				.withClaim("id", String.valueOf(user.getId()))
				.withClaim("account", user.getAccount())
				.withClaim("username", user.getUsername())
				// 设置过期时间
				.withExpiresAt(expireDate).sign(algo);
		return token;
	}

	/**
	 * 解析token为用户
	 *
	 * @param token token
	 * @return 用户
	 */
	public User parseToken(String token) {
		Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET_KEY);
		JWTVerifier verifier = JWT.require(algorithm).withIssuer(DEFAULT_ISSUER).build();
		DecodedJWT jwt = verifier.verify(token);
		Map<String, Claim> claimMap = jwt.getClaims();
		User user = new User();
		user.setId(Long.valueOf(claimMap.get("id").asString()));
		user.setAccount(claimMap.get("account").asString());
		user.setUsername(claimMap.get("username").asString());
		return user;
	}

	/**
	 * 获取token过期日期
	 *
	 * @param token token
	 * @return 过期时间
	 */
	public Date getExpireDate(String token) {
		Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET_KEY);
		JWTVerifier verifier = JWT.require(algorithm).withIssuer(DEFAULT_ISSUER).build();
		DecodedJWT jwt = verifier.verify(token);
		return jwt.getExpiresAt();
	}

	/**
	 * 获取payload信息
	 *
	 * @param token    token
	 * @param claimKey key
	 * @return 获取key信息
	 */
	public String getClaim(String token, String claimKey) {
		Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET_KEY);
		JWTVerifier verifier = JWT.require(algorithm).withIssuer(DEFAULT_ISSUER).build();
		DecodedJWT jwt = verifier.verify(token);
		Claim claim = jwt.getClaim(claimKey);
		return claim.asString();
	}

	/**
	 * accessToke refreshToke 校验
	 */
	public ResponseResult accessRefreshTokeValidate(String accessToken, String refreshToken) {
		if (StringUtils.isBlank(accessToken)) {
			return ResponseResult.fail("accesstoken is not exist.");
		}
		if (StringUtils.isBlank(refreshToken)) {
			return ResponseResult.fail("refreshtoken is not exist.");
		}
		String accessuser = JSON.toJSONString(parseToken(accessToken));
		String refreshuser = JSON.toJSONString(parseToken(refreshToken));
		// 两个token不匹配
		if (!accessuser.equals(refreshuser)) {
			return ResponseResult.fail("accesstoken and refreshtoken not match.");
		}
		return ResponseResult.success();
	}

}
