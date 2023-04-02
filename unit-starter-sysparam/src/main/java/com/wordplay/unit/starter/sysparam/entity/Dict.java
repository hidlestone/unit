package com.wordplay.unit.starter.sysparam.entity;

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
@TableName(value = "s_dict")
public class Dict extends BaseEntity {

	private static final long serialVersionUID = -1048086286087613537L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 字典编码
	 */
	@TableField(value = "`code`")
	private String code;

	/**
	 * 字典值
	 */
	@TableField(value = "`value`")
	private String value;

	/**
	 * 字典描述
	 */
	@TableField(value = "`desc`")
	private String desc;

	/**
	 * 是否启用
	 */
	@TableField(value = "`status`")
	private StatusEnum status;

	/**
	 * 字典项明细
	 */
	@TableField(exist = false)
	private List<DictDtl> dictDtlList;

}