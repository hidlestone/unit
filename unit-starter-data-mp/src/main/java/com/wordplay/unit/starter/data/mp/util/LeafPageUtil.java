package com.wordplay.unit.starter.data.mp.util;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wordplay.unit.starter.api.model.Leaf;

import java.util.List;

/**
 * page转leaf工具类
 *
 * @author zhuangpf
 */
public class LeafPageUtil {

	/**
	 * page转换成leaf
	 *
	 * @param page mp分页
	 * @return leaf分页
	 */
	public static Leaf pageToLeaf(Page page) {
		Leaf leaf = new Leaf();
		leaf.setRecords(page.getRecords());
		leaf.setTotal(page.getTotal());
		leaf.setSize(page.getSize());
		leaf.setCurrent(page.getCurrent());
		return leaf;
	}

	/**
	 * page转换成leaf
	 *
	 * @param page  mp分页
	 * @param clazz 响应类的class
	 * @return leaf分页
	 */
	public static Leaf pageToLeaf(Page page, Class clazz) {
		Leaf leaf = new Leaf();
		if (null != clazz) {
			List recordList = JSON.parseArray(JSON.toJSONString(page.getRecords()), clazz);
			leaf.setRecords(recordList);
		} else {
			throw new RuntimeException("paramter class is not exist");
		}
		leaf.setTotal(page.getTotal());
		leaf.setCurrent(page.getCurrent());
		return leaf;
	}

	public static Leaf leafToType(Leaf leaf, Class clazz) {
		List records = leaf.getRecords();
		if (null == records || records.size() == 0) {
			return leaf;
		}
		if (null != clazz) {
			try {
				List list = JSON.parseArray(JSON.toJSONString(records), clazz);
				leaf.setRecords(list);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			throw new RuntimeException("paramter class is not exist");
		}
		return leaf;
	}

}
