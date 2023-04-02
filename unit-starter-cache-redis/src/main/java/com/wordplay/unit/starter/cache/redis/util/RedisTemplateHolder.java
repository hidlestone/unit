package com.wordplay.unit.starter.cache.redis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 用于获取redisTemplate
 *
 * @author zhuangpf
 */
@Component
public class RedisTemplateHolder {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	public RedisTemplate<String, Object> getRedisTemplate() {
		return this.redisTemplate;
	}
}
