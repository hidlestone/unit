package com.wordplay.unit.starter.i18n.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.api.response.ResponseResult;
import com.wordplay.unit.starter.cache.redis.util.RedisUtil;
import com.wordplay.unit.starter.data.mp.util.LeafPageUtil;
import com.wordplay.unit.starter.i18n.constant.I18nStarterConstant;
import com.wordplay.unit.starter.i18n.entity.I18nResource;
import com.wordplay.unit.starter.i18n.mapper.I18nResourceMapper;
import com.wordplay.unit.starter.i18n.service.I18nResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class I18nResourceServiceImpl extends ServiceImpl<I18nResourceMapper, I18nResource> implements I18nResourceService {

	@Autowired
	private RedisUtil redisUtil;

	@Override
	public Leaf<I18nResource> page(I18nResource i18nResource) {
		Page<I18nResource> page = new Page<>(i18nResource.getPageNum(), i18nResource.getPageSize());
		page = this.baseMapper.page(page, i18nResource);
		return LeafPageUtil.pageToLeaf(page);
	}

	@Override
	public List<I18nResource> getByResourceKey(String resourceKey) {
		List<I18nResource> i18nResourceList = list(Wrappers.lambdaQuery(I18nResource.class).eq(I18nResource::getResourceKey, resourceKey));
		return i18nResourceList;
	}

	/**
	 * 刷新多语言词条缓存
	 */
	@Override
	public ResponseResult refreshI18nResourceCache() {
		// 所有
		List<I18nResource> i18nResourceList = this.baseMapper.selectList(null);
		if (CollectionUtil.isNotEmpty(i18nResourceList)) {
			// 按照语言编码分组
			Map<String, List<I18nResource>> langCodeResMap = i18nResourceList.stream().collect(Collectors.groupingBy(it -> it.getLangCode()));
			if (CollectionUtil.isNotEmpty(langCodeResMap)) {
				for (Map.Entry<String, List<I18nResource>> entry : langCodeResMap.entrySet()) {
					// 删除语言词条缓存：i18n:resource:en
					redisUtil.del(I18nStarterConstant.CACHE_KEY_I18N + entry.getKey());
					for (I18nResource i18nResource : entry.getValue()) {
						redisUtil.hset(I18nStarterConstant.CACHE_KEY_I18N + entry.getKey(), i18nResource.getResourceKey(), i18nResource.getResourceValue());
					}
				}
			}
		}
		return ResponseResult.success();
	}

}
