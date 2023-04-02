package com.wordplay.unit.starter.shiro.model;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wordplay.unit.starter.api.model.StatusEnum;
import com.wordplay.unit.starter.cache.redis.util.RedisUtil;
import com.wordplay.unit.starter.rbac.constant.RbacStarterConstant;
import com.wordplay.unit.starter.rbac.entity.Permission;
import com.wordplay.unit.starter.rbac.entity.Role;
import com.wordplay.unit.starter.rbac.entity.User;
import com.wordplay.unit.starter.rbac.service.PermissionService;
import com.wordplay.unit.starter.rbac.service.RoleService;
import com.wordplay.unit.starter.rbac.service.UserService;
import com.wordplay.unit.starter.shiro.custom.JWTToken;
import com.wordplay.unit.starter.shiro.util.JWTUtil;
import com.wordplay.unit.starter.util.base.EncryptionUtil;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 认证授权对象
 *
 * @author zhuangpf
 */
public class ShiroRealm extends AuthorizingRealm {

	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private JWTUtil jwtUtil;

	/**
	 * 大坑，必须重写此方法，不然Shiro会报错
	 */
	@Override
	public boolean supports(AuthenticationToken authenticationToken) {
		return authenticationToken instanceof JWTToken
				|| authenticationToken instanceof UsernamePasswordToken
				|| authenticationToken instanceof AccountLoginToken;
	}

	/**
	 * 登录验证<br/>
	 * give authority to login
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		SimpleAuthenticationInfo info = null;
		// 登录账号密码验证
		if (token instanceof AccountLoginToken) {
			String account = (String) token.getPrincipal();
			String password = new String((char[]) token.getCredentials());
			QueryWrapper<User> wrapper = new QueryWrapper<>();
			// 账号密码登录
			if (LoginTypeEnum.PASSWORD.equals(((AccountLoginToken) token).getLoginType())) {
				wrapper.eq("account", account);
				wrapper.eq("password", password);
			} else if (LoginTypeEnum.VERIFICATIONCODE.equals(((AccountLoginToken) token).getLoginType())) {
				String verificationCode = (String) redisUtil.get(RbacStarterConstant.CACHE_KEY_USER_VERIFICATIONCODE + EncryptionUtil.encryptMD5(account));
				if (!verificationCode.equals(((AccountLoginToken) token).getVerificationCode())) {
					throw new UnknownAccountException("verification code is not correct.");
				}
				wrapper.eq("account", account);
			} else if (LoginTypeEnum.VERIFICATIONCODE.equals(((AccountLoginToken) token).getLoginType())) {
				// TODO
			}
			User user = userService.getOne(wrapper);
			if (null == user) {
				// 用户名或者密码错误
				throw new UnknownAccountException("account or password is not correct.");
			}
			if (StatusEnum.N.equals(user.getStatus())) {
				throw new LockedAccountException("account is disable, contact the admin.");
			}
			// 生成accesstoken
			info = new SimpleAuthenticationInfo(user, password, getName());
		} else if (token instanceof JWTToken) {
			// JWTToken
			String username = ((JWTToken) token).getPrincipal();
			String accesstoken = ((JWTToken) token).getCredentials();
			// 用户id
			String idStr = jwtUtil.getClaim(accesstoken, "id");
			String accesstokenCache = (String) redisUtil.get(RbacStarterConstant.CACHE_KEY_ACCESSTOKEN + idStr);
			if (!accesstoken.equals(accesstokenCache)) {
				throw new UnknownAccountException("invalid accesstoken");
			}
			info = new SimpleAuthenticationInfo(idStr + "#" + username, accesstoken, getName());
		} else {
			throw new RuntimeException("authenticationToken is not support");
		}
		return info;
	}

	/**
	 * 获取角色和权限<br/>
	 * give permission to deal auth_required method
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		if (null == principalCollection) {
			throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
		}
		String idUsername = (String) getAvailablePrincipal(principalCollection);
		Long id = Long.valueOf(idUsername.split("#")[0]);
		// 用户角色集
		List<Role> roleList = roleService.getRolesByUserId(id);
		Set<String> roleSet = new HashSet<String>();
		for (Role role : roleList) {
			roleSet.add(role.getRoleCode());
		}
		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		simpleAuthorizationInfo.setRoles(roleSet);
		// 用户权限集
		List<Permission> permissionResponseList = permissionService.getPermissionListByUserId(id);
		Set<String> permissionSet = new HashSet<>();
		for (Permission permission : permissionResponseList) {
			permissionSet.add(permission.getPermissionCode());
		}
		simpleAuthorizationInfo.setStringPermissions(permissionSet);
		return simpleAuthorizationInfo;
	}

}
