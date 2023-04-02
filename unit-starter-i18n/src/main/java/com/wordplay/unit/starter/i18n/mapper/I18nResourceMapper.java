package com.wordplay.unit.starter.i18n.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wordplay.unit.starter.i18n.entity.I18nResource;

public interface I18nResourceMapper extends BaseMapper<I18nResource> {

	Page<I18nResource> page(Page<I18nResource> page, I18nResource i18nResource);

}