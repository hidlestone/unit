package com.wordplay.unit.starter.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wordplay.unit.starter.data.mp.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName(value = "s_sms_config")
public class SmsConfig extends BaseEntity {

	private static final long serialVersionUID = 8639102899535794831L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 使用的短信产品类型
	 */
	@TableField(value = "product_type")
	private String productType;

	/**
	 * 区域ID
	 */
	@TableField(value = "region_id")
	private String regionId;

	/**
	 * accessKeyId
	 */
	@TableField(value = "access_key_id")
	private String accessKeyId;

	/**
	 * accessKeySecret
	 */
	@TableField(value = "access_key_secret")
	private String accessKeySecret;

	/**
	 * 其他的参数配置(JSON格式)
	 */
	@TableField(value = "properties")
	private String properties;

}