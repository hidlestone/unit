package com.wordplay.unit.starter.cache.simple;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 使用 ConcurrentHashMap 实现简单的缓存
 *
 * @author zhuangpf
 */
public class SimpleMemoryCacheImpl {

	// 缓存：键-值
	private static final Map<String, Object> CACHE = new ConcurrentHashMap();
	// 缓存：键-ttl，-1表示永久
	private static final Map<String, Long> TTL = new ConcurrentHashMap();
	// 更新缓存线程
	private static final ScheduledExecutorService TTL_EXECUTOR = Executors.newSingleThreadScheduledExecutor();
	// 单实例
	private static final SimpleMemoryCacheImpl INSTANCE = new SimpleMemoryCacheImpl();

	public static SimpleMemoryCacheImpl getInstance() {
		return INSTANCE;
	}

	public Object get(String key) {
		lazyDelete(new HashSet<String>() {{
			add(key);
		}});
		return CACHE.get(key);
	}

	public void put(String key, Object value) {
		CACHE.put(key, value);
		TTL.put(key, -1L);
	}

	public void put(String key, Object value, int timeToLiveSeconds) {
		CACHE.put(key, value);
		if (-1 == timeToLiveSeconds) {
			TTL.put(key, -1L);
		} else {
			TTL.put(key, System.currentTimeMillis() + (long) (timeToLiveSeconds * 1000));
		}
	}

	public void remove(String... key) {
		if (key != null && key.length != 0) {
			String[] arr = key;
			int len = key.length;
			for (int i = 0; i < len; ++i) {
				String k = arr[i];
				CACHE.remove(k);
				TTL.remove(k);
			}
		}
	}

	public void remove(Set<String> keys) {
		Iterator iterator = keys.iterator();
		while (iterator.hasNext()) {
			String k = (String) iterator.next();
			CACHE.remove(k);
			TTL.remove(k);
		}
	}

	public Set<String> getKeysStartWith(String prefix) {
		Set<String> resultSet = new HashSet<>();
		Set<Map.Entry<String, Long>> entrySet = TTL.entrySet().stream().filter((entry) -> {
			// prefix开头且ttl大于当前时间
			return entry.getKey().startsWith(prefix) && (entry.getValue() > System.currentTimeMillis() || -1L == entry.getValue());
		}).collect(Collectors.toSet());
		for (Map.Entry<String, Long> entry : entrySet) {
			resultSet.add(entry.getKey());
		}
		return resultSet;
	}

	public List<Object> getValuesStartWith(String prefix) {
		Set<String> keys = this.getKeysStartWith(prefix);
		List<Object> list = new ArrayList();
		Iterator iterator = keys.iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			Object obj = CACHE.get(key);
			if (null != obj) {
				list.add(obj);
			}
		}
		return list;
	}

	public Boolean expire(String key, int timeToLive) {
		if (-1 == timeToLive) {
			TTL.put(key, -1L);
		} else {
			TTL.put(key, System.currentTimeMillis() + (long) timeToLive);
		}
		return Boolean.TRUE;
	}

	public List<String> getKeysByPattern(String... pattern) {
		if (pattern != null && pattern.length != 0) {
			Set<String> allKeys = CACHE.keySet();
			List<String> set = new ArrayList();
			Iterator iterator = allKeys.iterator();

			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				String[] patternArr = pattern;
				int len = pattern.length;
				for (int i = 0; i < len; ++i) {
					String p = patternArr[i];
					if (p.startsWith("*") && p.endsWith("*")) {
						if (key.contains(p.substring(1, p.length() - 1))) {
							set.add(key);
						}
					} else if (p.startsWith("*")) {
						if (key.endsWith(p.substring(1))) {
							set.add(key);
						}
					} else if (p.endsWith("*") && key.startsWith(p.substring(0, p.length() - 1))) {
						set.add(key);
					}
				}
			}

			return set;
		} else {
			return new ArrayList();
		}
	}

	public List<Object> get(List<String> keys) {
		List<Object> list = new ArrayList();
		lazyDelete(new HashSet<String>(keys));
		Iterator iterator = keys.iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			Object obj = CACHE.get(key);
			if (null != obj) {
				list.add(obj);
			}
		}
		return list;
	}

	/**
	 * 主动清理策略，每秒
	 */
	public void startTtlMonitor() {
		TTL_EXECUTOR.scheduleAtFixedRate(new Runnable() {
			public void run() {
				Set<String> keys = SimpleMemoryCacheImpl.TTL.keySet();
				lazyDelete(keys);
			}
		}, 0L, 5L, TimeUnit.SECONDS);
	}

	/**
	 * 惰性删除策略
	 */
	public void lazyDelete(Set<String> keys) {
		Iterator iterator = keys.iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			Long ttl = (Long) SimpleMemoryCacheImpl.TTL.get(key);
			if (-1 != ttl) {
				if (ttl != null && ttl < System.currentTimeMillis()) {
					SimpleMemoryCacheImpl.TTL.remove(key);
					SimpleMemoryCacheImpl.CACHE.remove(key);
				} else if (ttl == null) {
					SimpleMemoryCacheImpl.CACHE.remove(key);
				} else if (!SimpleMemoryCacheImpl.CACHE.containsKey(key)) {
					SimpleMemoryCacheImpl.TTL.remove(key);
				}
			}
		}
	}

}
