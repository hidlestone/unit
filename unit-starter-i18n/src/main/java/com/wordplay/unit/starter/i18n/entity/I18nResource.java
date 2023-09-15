package com.wordplay.unit.starter.i18n.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wordplay.unit.starter.data.mp.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName(value = "s_i18n_resource")
public class I18nResource extends BaseEntity {

	private static final long serialVersionUID = -2437250951416088118L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 语言编码
	 */
	@TableField(value = "lang_code")
	private String langCode;

	/**
	 * 资源key
	 */
	@TableField(value = "resource_key")
	private String resourceKey;

	/**
	 * 资源value
	 */
	@TableField(value = "resource_value")
	private String resourceValue;

	/**
	 * 所属module
	 */
	@TableField(value = "module_code")
	private String moduleCode;

	/**
	 * 源语言编码
	 */
	@TableField(exist = false)
	private String sourceLangCode;

	/**
	 * 目标语言编码
	 */
	@TableField(exist = false)
	private String trargetLangCode;

}
