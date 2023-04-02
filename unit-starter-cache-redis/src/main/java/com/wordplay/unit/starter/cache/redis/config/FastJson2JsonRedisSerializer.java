package com.wordplay.unit.starter.cache.redis.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;

/**
 * FastJson序列化器
 *
 * @author zhuangpf
 */
public class FastJson2JsonRedisSerializer<T> implements RedisSerializer<T> {

	// 默认字符集
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	private Class<T> clazz;
	private static SerializeConfig serializeConfig = new SerializeConfig();

	public FastJson2JsonRedisSerializer(Class<T> clazz) {
		super();
		this.clazz = clazz;
	}

	/**
	 * 添加autotype白名单(fastjason漏洞)<br>
	 * 解决redis反序列化对象时报错 ：com.alibaba.fastjson.JSONException: autoType is not support
	 */
	static {
		// 全局开启AutoType，不建议使用
		ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
		// 建议使用这种方式，小范围指定白名单
		ParserConfig.getGlobalInstance().addAccept("com.***.entity");
	}

	/**
	 * 序列化
	 */
	public byte[] serialize(T t) throws SerializationException {
		return t == null ? new byte[0] : JSON.toJSONString(t, serializeConfig, new SerializerFeature[]{SerializerFeature.WriteClassName, SerializerFeature.WriteMapNullValue}).getBytes(DEFAULT_CHARSET);
	}

	/**
	 * 反序列化
	 */
	public T deserialize(byte[] bytes) throws SerializationException {
		if (bytes != null && bytes.length > 0) {
			String str = new String(bytes, DEFAULT_CHARSET);
			try {
				return JSON.parseObject(str, this.clazz);
			} catch (Exception var4) {
				return (T) str;
			}
		} else {
			return null;
		}
	}

}
