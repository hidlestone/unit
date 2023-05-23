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
@TableName(value = "s_role_permission")
public class RolePermission extends BaseEntity {

	private static final long serialVersionUID = 6105909801790260982L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 角色ID
	 */
	@TableField(value = "role_id")
	private Long roleId;

	/**
	 * 权限ID
	 */
	@TableField(value = "permission_id")
	private Long permissionId;

	/**
	 * 角色编码
	 */
	@TableField(exist = false)
	private String roleCode;

	/**
	 * 权限编码
	 */
	@TableField(exist = false)
	private String permissionCode;

	/**
	 * 资源值即URL
	 */
	@TableField(exist = false)
	private String resourceValue;

	/**
	 * 认证类型：anon(允许匿名访问)，auth(登录即可访问)
	 */
	@TableField(exist = false)
	private AuthcTypeEnum authcType;
}