package com.wordplay.unit.starter.cache.redis.util;

import com.wordplay.unit.starter.cache.redis.config.FastJson2JsonRedisSerializer;
import com.wordplay.unit.starter.cache.redis.config.StringRedisSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author zhuangpf
 */
@Component
public class RedisUtil {

	/**
	 * 使用bean：redisTemplate
	 */
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	// 字符串序列化器
	private static final RedisSerializer<Object> STRING_REDIS_SERIALIZER = new StringRedisSerializer();
	// json字符串序列化器
	private static final RedisSerializer<Object> OBJECT_REDIS_SERIALIZER = new FastJson2JsonRedisSerializer(Object.class);

	/**
	 * 重新设置key过期时间
	 */
	public boolean expire(String key, long time) {
		try {
			this.redisTemplate.expire(key, time, TimeUnit.SECONDS);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 获取过期时间
	 */
	public long getExpire(String key) {
		return this.redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}

	/**
	 * key是否存在
	 */
	public boolean hasKey(String key) {
		try {
			return this.redisTemplate.hasKey(key);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 根据key删除
	 */
	public void del(String key) {
		this.redisTemplate.delete(key);
	}

	/**
	 * 根据key删除
	 */
	public void del(String... key) {
		if (key != null && key.length > 0) {
			if (key.length == 1) {
				this.redisTemplate.delete(key[0]);
			} else {
				this.redisTemplate.delete(Arrays.asList(key));
			}
		}

	}

	/**
	 * 根据key删除
	 */
	public void del(Collection<String> keys) {
		this.redisTemplate.delete(keys);
	}

	/**
	 * 扫描
	 */
	public Set<String> scanKeys(String... pattern) {
		return (Set<String>) this.redisTemplate.execute((RedisCallback<Set<String>>) (connection) -> {
			Set<String> set = new HashSet();
			if (pattern != null && pattern.length != 0) {
				String[] patternArr = pattern;
				int len = pattern.length;
				for (int i = 0; i < len; ++i) {
					String p = patternArr[i];
					ScanOptions scanOptions = ScanOptions.scanOptions().match(p).count(100000L).build();
					Cursor cursor = connection.scan(scanOptions);
					while (cursor.hasNext()) {
						set.add((String) STRING_REDIS_SERIALIZER.deserialize((byte[]) cursor.next()));
					}
				}
				return set;
			} else {
				return set;
			}
		});
	}

	/**
	 * 扫描获取键值
	 */
	public Map<String, Object> scanValuesAsMap(String... pattern) {
		List<String> keys = new ArrayList(this.scanKeys(pattern));
		Map<String, Object> map = new HashMap();
		List<Object> list = this.scanValues(pattern);
		for (int i = 0; i < list.size(); ++i) {
			map.put(keys.get(i), list.get(i));
		}
		return map;
	}

	/**
	 * 扫描获取值
	 */
	public List<Object> scanValues(String... pattern) {
		List<String> keys = new ArrayList(this.scanKeys(pattern));
		return this.redisTemplate.executePipelined((RedisCallback<?>) (connection) -> {
			keys.forEach((key) -> {
				connection.get(STRING_REDIS_SERIALIZER.serialize(key));
			});
			return null;
		});
	}

	/**
	 * 获取值
	 */
	public List<Object> getValues(Collection<String> keys) {
		return this.redisTemplate.opsForValue().multiGet(keys);
	}

	/**
	 * 根据key获取值
	 */
	public Object get(String key) {
		return key == null ? null : this.redisTemplate.opsForValue().get(key);
	}

	/**
	 * 存储键值
	 */
	public boolean set(String key, Object value) {
		try {
			this.redisTemplate.opsForValue().set(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 存储键值ttl
	 */
	public boolean set(String key, Object value, long time) {
		try {
			if (time > 0L) {
				this.redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
			} else {
				this.set(key, value);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 递增
	 */
	public long incr(String key, long delta) {
		if (delta < 0L) {
			throw new RuntimeException("递增因子必须大于0");
		} else {
			return this.redisTemplate.opsForValue().increment(key, delta);
		}
	}

	/**
	 * 递减
	 */
	public long decr(String key, long delta) {
		if (delta < 0L) {
			throw new RuntimeException("递减因子必须大于0");
		} else {
			return this.redisTemplate.opsForValue().increment(key, -delta);
		}
	}

	/**
	 * 获取值
	 */
	public Object hget(String key, String item) {
		return this.redisTemplate.opsForHash().get(key, item);
	}

	/**
	 * key中的键值对
	 */
	public Map<Object, Object> hmget(String key) {
		return this.redisTemplate.opsForHash().entries(key);
	}

	/**
	 * 键集合
	 */
	public Set<Object> hmGetKeys(String key) {
		return this.redisTemplate.opsForHash().entries(key).keySet();
	}

	/**
	 * 存储hash
	 */
	public boolean hmset(String key, Map<String, Object> map) {
		try {
			this.redisTemplate.opsForHash().putAll(key, map);
			return true;
		} catch (Exception var4) {
			var4.printStackTrace();
			return false;
		}
	}

	/**
	 * 存储hash ttl
	 */
	public boolean hmset(String key, Map<String, Object> map, long time) {
		try {
			this.redisTemplate.opsForHash().putAll(key, map);
			if (time > 0L) {
				this.expire(key, time);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 存储hash(key item value)
	 */
	public boolean hset(String key, String item, Object value) {
		try {
			this.redisTemplate.opsForHash().put(key, item, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 使用pipelining(管道) 存储hash
	 */
	public boolean pipelineHset(String key, Map<String, Object> values) {
		try {
			this.redisTemplate.executePipelined((RedisCallback<?>) (connection) -> {
				values.forEach((k, v) -> {
					connection.hSet(STRING_REDIS_SERIALIZER.serialize(key), STRING_REDIS_SERIALIZER.serialize(k), OBJECT_REDIS_SERIALIZER.serialize(v));
				});
				return null;
			});
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 使用pipelining(管道) 获取
	 */
	public Map<String, String> pipelineHget(String key, List<String> fields) {
		try {
			List<Object> list = this.redisTemplate.executePipelined((RedisCallback<?>) (connection) -> {
				fields.forEach((field) -> {
					connection.hGet(STRING_REDIS_SERIALIZER.serialize(key), STRING_REDIS_SERIALIZER.serialize(field));
				});
				return null;
			});
			Map<String, String> map = new HashMap();

			for (int i = 0; i < list.size(); ++i) {
				map.put(fields.get(i), list.get(i).toString());
			}

			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap();
		}
	}

	/**
	 * 存储hash
	 */
	public boolean hset(String key, String item, Object value, long time) {
		try {
			this.redisTemplate.opsForHash().put(key, item, value);
			if (time > 0L) {
				this.expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除
	 */
	public void hdel(String key, Object... item) {
		this.redisTemplate.opsForHash().delete(key, item);
	}

	/**
	 * 是否存在
	 */
	public boolean hHasKey(String key, String item) {
		return this.redisTemplate.opsForHash().hasKey(key, item);
	}

	/**
	 * 递增
	 */
	public double hincr(String key, String item, double by) {
		return this.redisTemplate.opsForHash().increment(key, item, by);
	}

	/**
	 * 递减
	 */
	public double hdecr(String key, String item, double by) {
		return this.redisTemplate.opsForHash().increment(key, item, -by);
	}

	/**
	 * 获取set
	 */
	public Set<Object> sGet(String key) {
		try {
			return this.redisTemplate.opsForSet().members(key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 是否存在
	 */
	public boolean isSetMember(String key, Object value) {
		try {
			return this.redisTemplate.opsForSet().isMember(key, value);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * set存储
	 */
	public long sSet(String key, Object... values) {
		try {
			return this.redisTemplate.opsForSet().add(key, values);
		} catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}

	/**
	 * set存储
	 */
	public long sSetAndTime(String key, long time, Object... values) {
		try {
			Long count = this.redisTemplate.opsForSet().add(key, values);
			if (time > 0L) {
				this.expire(key, time);
			}

			return count;
		} catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}

	/**
	 * set大小
	 */
	public long sGetSetSize(String key) {
		try {
			return this.redisTemplate.opsForSet().size(key);
		} catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}

	/**
	 * 删除
	 */
	public long setRemove(String key, Object... values) {
		try {
			Long count = this.redisTemplate.opsForSet().remove(key, values);
			return count;
		} catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}

	/**
	 * list获取
	 */
	public List<Object> lGet(String key, long start, long end) {
		try {
			return this.redisTemplate.opsForList().range(key, start, end);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * list大小
	 */
	public long lGetListSize(String key) {
		try {
			return this.redisTemplate.opsForList().size(key);
		} catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}

	/**
	 * 获取
	 */
	public Object lGetIndex(String key, long index) {
		try {
			return this.redisTemplate.opsForList().index(key, index);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * list存储
	 */
	public boolean lSet(String key, Object value) {
		try {
			this.redisTemplate.opsForList().rightPush(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 压入list左
	 */
	public boolean lSet(String key, Object value, long time) {
		try {
			this.redisTemplate.opsForList().rightPush(key, value);
			if (time > 0L) {
				this.expire(key, time);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 压入list左
	 */
	public boolean lSet(String key, List<Object> value) {
		try {
			this.redisTemplate.opsForList().rightPushAll(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 压入list左
	 */
	public boolean lSet(String key, List<Object> value, long time) {
		try {
			this.redisTemplate.opsForList().rightPushAll(key, value);
			if (time > 0L) {
				this.expire(key, time);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * list左出栈
	 */
	public Object lPop(String key) {
		try {
			return this.redisTemplate.opsForList().leftPop(key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 更新
	 */
	public boolean lUpdateIndex(String key, long index, Object value) {
		try {
			this.redisTemplate.opsForList().set(key, index, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除
	 */
	public long lRemove(String key, long count, Object value) {
		try {
			Long remove = this.redisTemplate.opsForList().remove(key, count, value);
			return remove;
		} catch (Exception e) {
			e.printStackTrace();
			return 0L;
		}
	}

	/**
	 * 获取所有的key
	 */
	public Set<String> keys(String pattern) {
		return redisTemplate.keys(pattern);
	}

}
