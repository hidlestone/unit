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
@TableName(value = "s_menu")
public class Menu extends BaseEntity {

	private static final long serialVersionUID = 1794877425243821610L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 父级菜单ID
	 */
	@TableField(value = "parent_id")
	private Long parentId;

	/**
	 * 菜单层级
	 */
	@TableField(value = "`level`")
	private Byte level;

	/**
	 * 菜单编码
	 */
	@TableField(value = "menu_code")
	private String menuCode;

	/**
	 * 菜单名称
	 */
	@TableField(value = "menu_name")
	private String menuName;

	/**
	 * 菜单描述
	 */
	@TableField(value = "menu_desc")
	private String menuDesc;

	/**
	 * 访问路径
	 */
	@TableField(value = "path")
	private String path;

	/**
	 * 功能链接如：/platform/msg-log
	 */
	@TableField(value = "func_link")
	private String funcLink;

	/**
	 * 打开方式，inner-link：通过链接打开tab，outter-link：通过链接打开浏览器新窗口，默认空
	 */
	@TableField(value = "open_type")
	private Byte openType;

	/**
	 * 图标
	 */
	@TableField(value = "icon")
	private String icon;

	/**
	 * 排序
	 */
	@TableField(value = "`order`")
	private Byte order;

	/**
	 * 备注
	 */
	@TableField(value = "remark")
	private String remark;

	/**
	 * 是否显示
	 */
	@TableField(value = "`status`")
	private StatusEnum status;

	/**
	 * 子菜单
	 */
	@TableField(exist = false)
	private List<Menu> children;

	/**
	 * 用户ID
	 */
	@TableField(exist = false)
	private Long userId;

	/**
	 * 角色ID列表
	 */
	@TableField(exist = false)
	private List<Long> roleIds;

}