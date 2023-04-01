package com.wordplay.unit.starter.data.mp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 对应数据库表公共字段：<br>
 * `create_user_id` bigint(20) DEFAULT NULL COMMENT '创建用户ID'<br>
 * `modify_user_id` bigint(20) DEFAULT NULL COMMENT '修改用户ID'<br>
 * `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'<br>
 * `gmt_modified` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更改时间'<br>
 *
 * @author zhuangpf
 * @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
 */
@Getter
@Setter
@TableName(value = "base_entity")
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = -2019856722533574712L;

	/**
	 * 多语言编码，默认中文
	 */
	@TableField(exist = false)
	private String langCode = "zh_CN";

	/**
	 * 页码。系统默认：start from 1。
	 */
	@TableField(exist = false)
	private Integer pageNum = 1;

	/**
	 * 条数
	 */
	@TableField(exist = false)
	private Integer pageSize = 10;

	/**
	 * 创建用户ID
	 */
	@TableField(value = "create_user_id")
	private Long createUserId;

	/**
	 * 修改用户ID
	 */
	@TableField(value = "modify_user_id")
	private Long modifyUserId;

	/**
	 * 创建时间
	 */
	@TableField(value = "gmt_create")
	private Date gmtCreate;

	/**
	 * 更改时间
	 */
	@TableField(value = "gmt_modified")
	private Date gmtModified;

	/**
	 * 起始创建时间
	 */
	@TableField(exist = false)
	private Date gmtCreateStart;

	/**
	 * 结束创建时间
	 */
	@TableField(exist = false)
	private Date gmtCreateEnd;

	/**
	 * 起始更改时间
	 */
	@TableField(exist = false)
	private Date gmtModifiedStart;

	/**
	 * 结束更改时间
	 */
	@TableField(exist = false)
	private Date gmtModifiedEnd;

	/**
	 * 第一条记录行号
	 */
	public Integer firstRowNum() {
		return (pageNum - 1) * pageSize;
	}

}
