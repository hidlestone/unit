package com.wordplay.unit.starter.rbac.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wordplay.unit.starter.data.mp.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName(value = "s_user_role")
public class UserRole extends BaseEntity {

	private static final long serialVersionUID = -810036528638990629L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 用户ID
	 */
	@TableField(value = "user_id")
	private Long userId;

	/**
	 * 角色ID
	 */
	@TableField(value = "role_id")
	private Long roleId;

}