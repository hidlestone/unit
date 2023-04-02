package com.wordplay.unit.starter.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wordplay.unit.starter.api.model.StatusEnum;
import com.wordplay.unit.starter.data.mp.BaseEntity;
import com.wordplay.unit.starter.file.model.StorageTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName(value = "s_file_info")
public class FileInfo extends BaseEntity {

	private static final long serialVersionUID = 9008324671673485598L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 文件组ID
	 */
	@TableField(value = "file_group_id")
	private Long fileGroupId;

	/**
	 * 文件名
	 */
	@TableField(value = "`name`")
	private String name;

	/**
	 * 无意义名称
	 */
	@TableField(value = "`nonsense_name`")
	private String nonsenseName;

	/**
	 * 文件扩展名
	 */
	@TableField(value = "extension")
	private String extension;

	/**
	 * 存储类型
	 */
	@TableField(value = "storage_type")
	private StorageTypeEnum storageType;

	/**
	 * 文件url
	 */
	@TableField(value = "url")
	private String url;

	/**
	 * 文件状态
	 */
	@TableField(value = "`status`")
	private StatusEnum status;

}