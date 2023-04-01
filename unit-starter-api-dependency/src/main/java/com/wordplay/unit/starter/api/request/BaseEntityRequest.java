package com.wordplay.unit.starter.api.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础请求参数类
 *
 * @author zhuangpf
 */
@Getter
@Setter
public class BaseEntityRequest implements Serializable {

	private static final long serialVersionUID = -3908244974737701868L;

	@ApiModelProperty("页码")
	private Integer pageNum = 1;

	@ApiModelProperty("条数")
	private Integer pageSize = 10;

	@ApiModelProperty("多语言编码")
	private String langCode = "zh_CN";

	@ApiModelProperty("创建用户ID")
	private Long createUserId;

	@ApiModelProperty("修改用户ID")
	private Long modifyUserId;

	@ApiModelProperty("创建时间")
	private Date gmtCreate;

	@ApiModelProperty("更改时间")
	private Date gmtModified;

	@ApiModelProperty("起始创建时间")
	private Date gmtCreateStart;

	@ApiModelProperty("结束创建时间")
	private Date gmtCreateEnd;

	@ApiModelProperty("起始更改时间")
	private Date gmtModifiedStart;

	@ApiModelProperty("结束更改时间")
	private Date gmtModifiedEnd;

	/**
	 * 第一条记录行号
	 */
	public Integer firstRowNum() {
		return (pageNum - 1) * pageSize;
	}

}
