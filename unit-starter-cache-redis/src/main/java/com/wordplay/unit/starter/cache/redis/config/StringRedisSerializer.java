package com.wordplay.unit.starter.cache.redis.config;

import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

import java.nio.charset.Charset;

/**
 * 使用StringRedisSerializer做key的序列化时，StringRedisSerializer的泛型指定的是String，传其他对象就会报类型转换错误，<br>
 * 在使用@Cacheable注解是key属性就只能传String进来。把这个序列化方式重写了，将泛型改成Object。
 *
 * @author zhuangpf
 */
public class StringRedisSerializer implements RedisSerializer<Object> {

	// 字符集
	private final Charset charset;
	// " 
	private final String target = "\"";
	// 空字符串
	private final String replacement = "";

	public StringRedisSerializer() {
		this(Charset.forName("UTF-8"));
	}

	public StringRedisSerializer(Charset charset) {
		Assert.notNull(charset, "Charset must not be null!");
		this.charset = charset;
	}

	@Override
	public String deserialize(byte[] bytes) {
		return (bytes == null ? null : new String(bytes, charset));
	}

	@Override
	public byte[] serialize(Object object) {
		String jsonStr = JSON.toJSONString(object);
		if (jsonStr == null) {
			return null;
		}
		jsonStr = jsonStr.replace(target, replacement);
		return jsonStr.getBytes(charset);
	}
}
