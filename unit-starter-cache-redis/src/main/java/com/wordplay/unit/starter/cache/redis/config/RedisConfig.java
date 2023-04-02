package com.wordplay.unit.starter.cache.redis.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

/**
 * Redis配置
 *
 * @author zhuangpf
 */
@Configuration
@EnableCaching
public class RedisConfig {

	/**
	 * bean：redisTemplate，使用FastJson进行序列化
	 */
	@Bean(name = {"redisTemplate"})
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		// redis开启事务
		// redisTemplate.setEnableTransactionSupport(true);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(new FastJson2JsonRedisSerializer(Object.class));
		redisTemplate.setValueSerializer(new FastJson2JsonRedisSerializer(Object.class));
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	/**
	 * bean：jdkRedisTemplate，使用默认的JdkSerializationRedisSerializer进行序列化
	 */
	@Bean(name = {"jdkRedisTemplate"})
	public RedisTemplate<String, Object> jdkRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashKeySerializer(redisTemplate.getKeySerializer());
		redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	/**
	 * 缓存管理器
	 */
	@Bean({"redisCacheManager"})
	@Primary
	public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
		RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
		FastJson2JsonRedisSerializer<Object> jsonSerializer = new FastJson2JsonRedisSerializer(Object.class);
		RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer);
		RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair);
		return new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
	}

}
