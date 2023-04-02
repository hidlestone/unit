package com.wordplay.unit.starter.i18n.service;

import com.wordplay.unit.starter.i18n.entity.I18nResource;
import com.wordplay.unit.starter.i18n.model.TranslateDto;

/**
 * @author zhuangpf
 */
public interface TranslateService {

	/**
	 * 翻译词条
	 *
	 * @param dto 请求参数
	 * @return 多语言词条
	 */
	I18nResource translate(TranslateDto dto);

}
