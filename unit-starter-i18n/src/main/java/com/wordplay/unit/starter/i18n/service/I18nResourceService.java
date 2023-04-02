package com.wordplay.unit.starter.i18n.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wordplay.unit.starter.api.model.Leaf;
import com.wordplay.unit.starter.api.response.ResponseResult;
import com.wordplay.unit.starter.i18n.entity.I18nResource;

import java.util.List;

public interface I18nResourceService extends IService<I18nResource> {

	Leaf<I18nResource> page(I18nResource i18nResource);

	List<I18nResource> getByResourceKey(String resourceKey);

	ResponseResult refreshI18nResourceCache();
	
}
