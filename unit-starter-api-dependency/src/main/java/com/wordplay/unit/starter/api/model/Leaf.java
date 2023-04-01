package com.wordplay.unit.starter.api.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 返回前端的分页查询数据
 *
 * @author zhuangpf
 */
@Getter
@Setter
public class Leaf<T> implements Serializable {

	private static final long serialVersionUID = -4371588110597992171L;

	/**
	 * 分页数据
	 */
	protected List<T> records;
	/**
	 * 总记录数
	 */
	protected Long total;
	/**
	 * 总页数
	 */
	protected Long size;
	/**
	 * 当前页码
	 */
	protected Long current;

	public Leaf() {
	}

	public Leaf(List<T> records, Long total, Long size, Long current) {
		this.records = records;
		this.total = total;
		this.size = size;
		this.current = current;
	}

}
