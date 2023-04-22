package com.wordplay.unit.starter.control.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * 基础control TODO
 *
 * @author zhuangpf
 * @date 2023-04-21
 */
public class BaseControl {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseControl.class);

	protected boolean isAdmin() {
		return Arrays.asList(this.getRoleCodes().split(",")).contains("admin");
	}

	protected String getRoleCodes() {
		return this.getUserAuthInfo().getRoleCodes();
	}
	
	
}
