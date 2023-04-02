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
@TableName(value = "s_sms_template")
public class SmsTemplate extends BaseEntity {

	private static final long serialVersionUID = 276155106026705243L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 短信模板配置描述
	 */
	@TableField(value = "`desc`")
	private String desc;

	/**
	 * 发送号码
	 */
	@TableField(value = "`from`")
	private String from;

	/**
	 * 0-简单，1-模板
	 */
	@TableField(value = "content_type")
	private Byte contentType;

	/**
	 * 内容
	 */
	@TableField(value = "content")
	private String content;

	/**
	 * 文件组ID
	 */
	@TableField(value = "file_group_id")
	private Long fileGroupId;

	/**
	 * 重试次数
	 */
	@TableField(value = "retry_count")
	private Byte retryCount;

}
