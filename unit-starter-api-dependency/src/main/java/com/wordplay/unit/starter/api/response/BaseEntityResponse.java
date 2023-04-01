package com.wordplay.unit.starter.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础响应参数类
 *
 * @author zhuangpf
 */
@Getter
@Setter
public class BaseEntityResponse implements Serializable {

	private static final long serialVersionUID = 4349971885970231113L;

	@ApiModelProperty("创建用户ID")
	private Long createUserId;

	@ApiModelProperty("修改用户ID")
	private Long modifyUserId;

	// kk表示24小时制，HH表示12小时制
	@JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
	@ApiModelProperty("创建时间")
	private Date gmtCreate;

	@JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
	@ApiModelProperty("更改时间")
	private Date gmtModified;

}
