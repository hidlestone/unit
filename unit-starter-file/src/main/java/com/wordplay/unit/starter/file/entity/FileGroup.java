package com.wordplay.unit.starter.file.entity;

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
@TableName(value = "s_file_group")
public class FileGroup extends BaseEntity {

	private static final long serialVersionUID = -5841600942384813666L;

	/**
	 * 主键
	 */
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 描述
	 */
	@TableField(value = "`desc`")
	private String desc;

	/**
	 * 文件组状态
	 */
	@TableField(value = "`status`")
	private StatusEnum status;

	/**
	 * 文件明细
	 */
	@TableField(exist = false)
	private List<FileInfo> fileInfos;

}