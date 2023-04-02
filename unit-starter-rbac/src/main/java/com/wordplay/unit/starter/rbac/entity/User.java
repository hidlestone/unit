package com.wordplay.unit.starter.rbac.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wordplay.unit.starter.api.model.StatusEnum;
import com.wordplay.unit.starter.data.mp.BaseEntity;
import com.wordplay.unit.starter.rbac.model.SexEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * UserDetails security 标准接口
 *
 * @author zhuangpf
 */
@Getter
@Setter
@TableName(value = "s_user")
public class User extends BaseEntity {

	private static final long serialVersionUID = -4088135606703627513L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 用户账号
	 */
	@TableField(value = "account")
	private String account;

	/**
	 * 用户密码
	 */
	@TableField(value = "password")
	private String password;

	/**
	 * 用户昵称
	 */
	@TableField(value = "username")
	private String username;

	/**
	 * 头像URL
	 */
	@TableField(value = "avatar")
	private String avatar;

	/**
	 * 电话号码
	 */
	@TableField(value = "tel")
	private String tel;

	/**
	 * 邮箱
	 */
	@TableField(value = "email")
	private String email;

	/**
	 * 身份证号
	 */
	@TableField(value = "id_card")
	private String idCard;

	/**
	 * 真实姓名
	 */
	@TableField(value = "name")
	private String name;

	/**
	 * 生日
	 */
	@TableField(value = "birthday")
	private Date birthday;

	/**
	 * 地址
	 */
	@TableField(value = "address")
	private String address;

	/**
	 * 性别
	 */
	@TableField(value = "sex")
	private SexEnum sex;

	/**
	 * 状态
	 */
	@TableField(value = "`status`")
	private StatusEnum status;

	/**
	 * 备注
	 */
	@TableField(value = "remark")
	private String remark;

	/**
	 * 用户来源
	 */
	@TableField(value = "source_type")
	private Byte sourceType;

	/**
	 * 最后登录时间
	 */
	@TableField(value = "last_login_time")
	private Date lastLoginTime;

}