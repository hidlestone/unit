package com.wordplay.unit.starter.i18n.service.impl;

import com.wordplay.unit.starter.i18n.entity.I18nResource;
import com.wordplay.unit.starter.i18n.model.TranslateDto;
import com.wordplay.unit.starter.i18n.model.langCodeEnum;
import com.wordplay.unit.starter.i18n.service.TranslateService;
import com.wordplay.unit.starter.i18n.util.TraditionalSimplifyConverter;
import org.springframework.stereotype.Service;

/**
 * 词条翻译工具类
 *
 * @author zhuangpf
 */
@Service
public class TranslateServiceImpl implements TranslateService {

	@Override
	public I18nResource translate(TranslateDto dto) {
		I18nResource i18nResourceResponse = new I18nResource();
		// 简体->繁体
		if (langCodeEnum.zh_CN.getCode().equals(dto.getSourceLangCode())
				&& langCodeEnum.zh_TW.getCode().equals(dto.getTrargetLangCode())) {
			String convertTraditional = TraditionalSimplifyConverter.convert(dto.getResourceValue(), TraditionalSimplifyConverter.TargetEnum.TRADITIONAL);
		}
		// 简体->en TODO 
		if (langCodeEnum.zh_CN.getCode().equals(dto.getSourceLangCode())
				&& langCodeEnum.en.getCode().equals(dto.getTrargetLangCode())) {

		}
		return i18nResourceResponse;
	}

}
