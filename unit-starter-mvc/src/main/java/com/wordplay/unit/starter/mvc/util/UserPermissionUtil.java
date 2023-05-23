package com.wordplay.unit.starter.mvc.util;

import cn.hutool.core.collection.CollectionUtil;
import com.wordplay.unit.starter.cache.redis.util.RedisUtil;
import com.wordplay.unit.starter.core.context.SexEnum;
import com.wordplay.unit.starter.core.context.StatusEnum;
import com.wordplay.unit.starter.core.context.UserAuthInfo;
import com.wordplay.unit.starter.rbac.entity.Role;
import com.wordplay.unit.starter.rbac.entity.User;
import com.wordplay.unit.starter.rbac.service.RoleService;
import com.wordplay.unit.starter.rbac.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户权限工具类
 *
 * @author zhuangpf
 * @date 2023-05-22
 */
@Component
public class UserPermissionUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserPermissionUtil.class);
	public static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

	private final static String USERAUTHINFO = "userauthinfo:";

	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private JWTUtil jwtUtil;
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;

	/**
	 * 获取用户登录授权信息
	 *
	 * @param token token
	 * @return
	 */
	public UserAuthInfo getUserAuthInfo(String token) {
		// 获取缓存中的用户登录授权信息
		String id = jwtUtil.getClaim(token, "id");
		UserAuthInfo userAuthInfo = (UserAuthInfo) redisUtil.get(USERAUTHINFO + id);
		if (null != userAuthInfo) {
			return userAuthInfo;
		}
		// 从数据库中查询
		User user = userService.getById(id);
		List<Role> roleList = roleService.getRolesByUserId(Long.valueOf(id));
		List<String> roleCodeList = null;
		if (CollectionUtil.isNotEmpty(roleList)) {
			roleCodeList = roleList.stream().map(e -> e.getRoleCode()).collect(Collectors.toList());
		}
		if (null == user) {
			return null;
		}
		userAuthInfo = new UserAuthInfo();
		userAuthInfo.setToken(token);
		userAuthInfo.setId(Long.valueOf(id));
		userAuthInfo.setAccount(user.getAccount());
		userAuthInfo.setUsername(user.getUsername());
		userAuthInfo.setAvatar(user.getAvatar());
		userAuthInfo.setSex(SexEnum.valueOf(user.getSex().name()));
		userAuthInfo.setStatus(StatusEnum.valueOf(user.getStatus().name()));
		userAuthInfo.setRoleCodeList(roleCodeList);
		redisUtil.set(USERAUTHINFO + id, userAuthInfo);
		return userAuthInfo;
	}

}
