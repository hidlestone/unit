package com.wordplay.unit.starter.rbac.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wordplay.unit.starter.api.model.StatusEnum;
import com.wordplay.unit.starter.data.mp.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@TableName("s_department")
public class Department extends BaseEntity {

	private static final long serialVersionUID = 6487609159550412843L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 上级部门ID
	 */
	@TableField("parent_id")
	private Long parentId;

	/**
	 * 部门编码
	 */
	@TableField("code")
	private String code;

	/**
	 * 部门名称
	 */
	@TableField("`name`")
	private String name;

	/**
	 * 部门描述
	 */
	@TableField("`desc`")
	private String desc;

	/**
	 * 排序
	 */
	@TableField(value = "`order`")
	private Byte order;

	/**
	 * 是否显示
	 */
	@TableField("`status`")
	private StatusEnum status;

	/**
	 * 子部门
	 */
	@TableField(exist = false)
	private List<Department> children;

}