package com.wordplay.unit.starter.cache.ehcache.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * EHCache 缓存工具类
 *
 * @author zhaungpf
 */
public class EHCacheUtil {

	public static CacheManager manager;
	public static String configfile;

	// EHCache初始化
	/*static {
		try {
			manager = CacheManager.create(EHCacheUtil.class.getClassLoader().getResourceAsStream(configfile));
		} catch (CacheException e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * 根据入参的ehcache配置文件进行构建
	 *
	 * @param filename 传入的ehcache配置文件
	 */
	public EHCacheUtil(String filename) {
		this.configfile = StringUtils.isEmpty(filename) ? "ehcache.xml" : filename;
		ClassLoader classLoader = getClass().getClassLoader();
		URL url = classLoader.getResource(configfile);
		// 绝对路径
		File file = new File(url.getFile());
		if (!file.exists()) {
			throw new RuntimeException("file " + configfile + " is not exist");
		}
		manager = CacheManager.create(EHCacheUtil.class.getClassLoader().getResourceAsStream(configfile));
	}

	/**
	 * 将数据存入Cache
	 *
	 * @param cachename Cache名称
	 * @param key       类似redis的Key
	 * @param value     类似redis的value，value可以是任何对象、数据类型，比如person,map,list等
	 */
	public void put(String cachename, Serializable key, Serializable value) {
		manager.getCache(cachename).put(new Element(key, value));
	}

	/**
	 * 获取该cachename下的所有缓存数据
	 *
	 * @param cachename Cache名称
	 * @return 该cachename下的所有缓存数据
	 */
	public Map<Object, Object> get(String cachename) {
		Map<Object, Object> resultMap = new HashMap();
		Cache cache = manager.getCache(cachename);
		Map<Object, Element> elementMap = cache.getAll(cache.getKeys());
		for (Map.Entry<Object, Element> entry : elementMap.entrySet()) {
			resultMap.put(entry.getKey(), entry.getValue().getObjectValue());
		}
		return resultMap;
	}

	/**
	 * 获取缓存cachename中key对应的value
	 *
	 * @param cachename Cache名称
	 * @param key       key
	 * @return 缓存数据
	 */
	public Object get(String cachename, Serializable key) {
		try {
			Element e = manager.getCache(cachename).get(key);
			if (e == null) {
				return null;
			}
			return e.getObjectValue();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (CacheException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 清除缓存名称为cachename的缓存
	 *
	 * @param cachename Cache名称
	 */
	public void clearCache(String cachename) {
		try {
			manager.getCache(cachename).removeAll();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 移除缓存cachename中key对应的value
	 *
	 * @param cachename Cache名称
	 * @param key       key
	 */
	public void remove(String cachename, Serializable key) {
		manager.getCache(cachename).remove(key);
	}

	public static CacheManager getManager() {
		return manager;
	}

	public static String getConfigfile() {
		return configfile;
	}

}
