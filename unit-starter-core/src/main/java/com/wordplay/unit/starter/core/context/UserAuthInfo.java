package com.wordplay.unit.starter.core.context;

import java.io.Serializable;
import java.util.List;

/**
 * 用户登录后的授权信息
 *
 * @author zhuangpf
 * @date 2023-04-22
 */
public class UserAuthInfo implements Serializable {

	private static final long serialVersionUID = -1807644994614557612L;

	// token
	private String token;
	// 用户账号
	private String account;
	// 用户昵称
	private String username;
	// 头像URL
	private String avatar;
	// 性别
	private SexEnum sex;
	// 状态
	private StatusEnum status;
	// 角色列表
	private List<String> roleCodeList;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public SexEnum getSex() {
		return sex;
	}

	public void setSex(SexEnum sex) {
		this.sex = sex;
	}

	public StatusEnum getStatus() {
		return status;
	}

	public void setStatus(StatusEnum status) {
		this.status = status;
	}

	public List<String> getRoleCodeList() {
		return roleCodeList;
	}

	public void setRoleCodeList(List<String> roleCodeList) {
		this.roleCodeList = roleCodeList;
	}
	
}
