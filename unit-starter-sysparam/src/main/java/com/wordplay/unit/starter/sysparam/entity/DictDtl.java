package com.wordplay.unit.starter.sysparam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wordplay.unit.starter.api.model.StatusEnum;
import com.wordplay.unit.starter.data.mp.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName(value = "s_dict_dtl")
public class DictDtl extends BaseEntity {

	private static final long serialVersionUID = -8971147452183256727L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 字典项ID
	 */
	@TableField(value = "dict_id")
	private Long dictId;

	/**
	 * 明细编码
	 */
	@TableField(value = "`code`")
	private String code;

	/**
	 * 明细值
	 */
	@TableField(value = "`value`")
	private String value;

	/**
	 * 明细描述
	 */
	@TableField(value = "`desc`")
	private String desc;

	/**
	 * 是否启用
	 */
	@TableField(value = "`status`")
	private StatusEnum status;

}
