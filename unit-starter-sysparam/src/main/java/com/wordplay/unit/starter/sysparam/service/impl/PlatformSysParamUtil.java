package com.wordplay.unit.starter.sysparam.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.wordplay.unit.starter.api.response.ResponseResult;
import com.wordplay.unit.starter.cache.redis.util.RedisUtil;
import com.wordplay.unit.starter.sysparam.constant.ConfigStarterConstant;
import com.wordplay.unit.starter.sysparam.entity.SysParamGroup;
import com.wordplay.unit.starter.sysparam.entity.SysParamItem;
import com.wordplay.unit.starter.sysparam.mapper.SysParamGroupMapper;
import com.wordplay.unit.starter.sysparam.mapper.SysParamItemMapper;
import com.wordplay.unit.starter.sysparam.service.SysParamGroupService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统参数服务工具类
 *
 * @author zhuangpf
 */
@Service
public class PlatformSysParamUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(PlatformSysParamUtil.class);

	private RedisUtil redisUtil;
	private SysParamGroupService sysParamGroupService;
	private SysParamItemMapper sysParamItemMapper;
	private SysParamGroupMapper sysParamGroupMapper;

	/**
	 * 初始化该实例的时候就去更新系统参数缓存<br/>
	 * 以便其他配置类在后续步骤可以获取到相关参数。
	 */
	public PlatformSysParamUtil(RedisUtil redisUtil, SysParamGroupService sysParamGroupService, SysParamItemMapper sysParamItemMapper, SysParamGroupMapper sysParamGroupMapper) {
		this.redisUtil = redisUtil;
		this.sysParamGroupService = sysParamGroupService;
		this.sysParamItemMapper = sysParamItemMapper;
		this.sysParamGroupMapper = sysParamGroupMapper;
		// 刷新系统参数缓存
		this.refreshSysParamCache();
	}

	/**
	 * 根据系统参数组编码，获取对应的系统参数
	 *
	 * @param groupCode 参数组编码
	 * @return 系统参数明细的map形式
	 */
	public ResponseResult<Map<String, String>> getSysParamGroupItemMap(String groupCode) {
		List<SysParamItem> sysParamItemList = (List<SysParamItem>) redisUtil.hget(ConfigStarterConstant.CACHE_KEY_SYS_PARAM, groupCode);
		if (CollectionUtil.isEmpty(sysParamItemList)) {
			SysParamGroup sysParamGroup = sysParamGroupService.get(groupCode);
			sysParamItemList = sysParamGroup.getSysParamItems();
		}
		if (CollectionUtil.isEmpty(sysParamItemList)) {
			// 系统参数未被初始化
			throw new RuntimeException("system param " + groupCode + " had not inited.");
		}
		Map<String, String> sysItemMap = sysParamItemList.stream().collect(Collectors.toMap(it -> it.getCode(), it -> it.getValue()));
		return ResponseResult.success(sysItemMap);
	}

	/**
	 * 系统参数明细项
	 *
	 * @param groupCode 参数组编码
	 * @param itemCode  明细项编码
	 * @return 系统参数明细项信息
	 */
	public ResponseResult<SysParamItem> getSysParamItem(String groupCode, String itemCode) {
		List<SysParamItem> sysParamItemList = (List<SysParamItem>) redisUtil.hget(ConfigStarterConstant.CACHE_KEY_SYS_PARAM, groupCode);
		SysParamItem sysParamItem = sysParamItemList.stream().filter(it -> itemCode.equals(it.getCode())).findFirst().get();
		if (null == sysParamItem) {
			SysParamItem sysParamItemTmp = sysParamItemMapper.selectById(itemCode);
			sysParamItem = sysParamItemTmp;
		}
		return ResponseResult.success(sysParamItem);
	}

	/**
	 * 刷新系统参数缓存
	 *
	 * @return 是否更新系统参数缓存成功
	 */
	public ResponseResult refreshSysParamCache() {
		List<SysParamGroup> sysParamGroupList = sysParamGroupMapper.getAllSysParamGroup();
		redisUtil.del(ConfigStarterConstant.CACHE_KEY_SYS_PARAM);
		for (SysParamGroup sysParamGroup : sysParamGroupList) {
			redisUtil.hset(ConfigStarterConstant.CACHE_KEY_SYS_PARAM, sysParamGroup.getCode(), sysParamGroup.getSysParamItems());
		}
		// 更新缓存成功
		LOGGER.info("system global parameters cache has refreshed.");
		return ResponseResult.success();
	}

	/**
	 * 获取系统参数，不存在则抛出异常
	 *
	 * @param sysItemMap   参数组
	 * @param sysParamItem 参数项
	 * @return 参数项中的值
	 */
	public String mapGet(Map<String, String> sysItemMap, String sysParamItem) {
		String value = sysItemMap.get(sysParamItem);
		if (StringUtils.isEmpty(value)) {
			// 未找到系统参数明细
			throw new RuntimeException("system param item " + sysParamItem + " is no exist.");
		}
		return value;
	}
}
