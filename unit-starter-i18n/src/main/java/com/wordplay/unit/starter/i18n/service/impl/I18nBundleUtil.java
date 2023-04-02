package com.wordplay.unit.starter.i18n.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wordplay.unit.starter.cache.redis.util.RedisUtil;
import com.wordplay.unit.starter.i18n.constant.I18nStarterConstant;
import com.wordplay.unit.starter.i18n.entity.I18nResource;
import com.wordplay.unit.starter.i18n.mapper.I18nResourceMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author zhuangpf
 */
@Service
public class I18nBundleUtil {

	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private I18nResourceMapper i18nResourceMapper;

	/**
	 * 获取支持的国际化类型
	 */
	public Set<Locale> getSupportedLoacle() {
		Set<String> langTypes = (Set) redisUtil.get(I18nStarterConstant.CACHE_KEY_I18N_SUPPORTED);
		Set<Locale> locales = new HashSet();
		for (String langType : langTypes) {
			locales.add(this.toLocale(langType));
		}
		return locales;
	}

	/**
	 * 根据名称获取国际化资源值
	 */
	public String getI18nResourceValue(String key, String langCode) {
		String resourcevalue = (String) redisUtil.hget(I18nStarterConstant.CACHE_KEY_I18N + langCode, key);
		if (StringUtils.isEmpty(resourcevalue)) {
			QueryWrapper<I18nResource> wrapper = new QueryWrapper();
			wrapper.eq("lang_code", langCode).eq("resource_key", key);
			I18nResource i18nResource = i18nResourceMapper.selectOne(wrapper);
			// 添加到缓存
			if (null != i18nResource) {
				redisUtil.hset(I18nStarterConstant.CACHE_KEY_I18N + langCode, i18nResource.getResourceKey(), i18nResource.getResourceValue());
				resourcevalue = i18nResource.getResourceValue();
			}
		}
		return StringUtils.isNotEmpty(resourcevalue) ? resourcevalue : key;
	}

	/**
	 * 转成Locale对象
	 */
	public static Locale toLocale(String langType) {
		if (!StringUtils.isBlank(langType) && !"null".equalsIgnoreCase(langType)) {
			if (langType.contains("_#")) {
				langType = langType.split("_#")[0];
			} else if (langType.contains("#")) {
				langType = langType.split("#")[0];
			}
			StringTokenizer st = new StringTokenizer(langType, "-_");
			return new Locale(st.nextToken(), st.hasMoreTokens() ? st.nextToken() : "", st.hasMoreTokens() ? st.nextToken() : "");
		} else {
			return null;
		}
	}

}
