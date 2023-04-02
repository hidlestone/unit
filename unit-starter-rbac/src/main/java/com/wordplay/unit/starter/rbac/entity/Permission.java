package com.wordplay.unit.starter.rbac.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wordplay.unit.starter.data.mp.BaseEntity;
import com.wordplay.unit.starter.rbac.model.AuthcTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName(value = "s_permission")
public class Permission extends BaseEntity {

	private static final long serialVersionUID = 3462421278968350566L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 权限编码
	 */
	@TableField(value = "permission_code")
	private String permissionCode;

	/**
	 * 权限名称即接口名称
	 */
	@TableField(value = "permission_name")
	private String permissionName;

	/**
	 * 资源值即URL
	 */
	@TableField(value = "resource_value")
	private String resourceValue;

	/**
	 * 顺序
	 */
	@TableField(value = "order_num")
	private Integer orderNum;

	/**
	 * 认证类型：anon(允许匿名访问)，auth(登录即可访问)
	 */
	@TableField(value = "authc_type")
	private AuthcTypeEnum authcType;

}