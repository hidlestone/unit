package com.wordplay.unit.starter.logback.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * mybatis-plus sql日志过滤器
 * 在file类型的日志中去除mp的日志
 *
 * @author zhuangpf
 */
public class MpSQLLoggerFilter extends Filter<ILoggingEvent> {

	@Override
	public FilterReply decide(ILoggingEvent event) {
		return "org.apache.ibatis.logging.stdout.StdOutImpl".equals(event.getLoggerName())
				? FilterReply.DENY : FilterReply.NEUTRAL;
	}

	
	/*
	com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor
	com.zaxxer.hikari.HikariDataSource

	event = {LoggingEvent@11745} "[DEBUG] ==>  Preparing: SELECT id,user_id,category_id,title,thumb_img_url,brief,content,editor_type,view_num,like_num,comment_num,is_allow_comment,status,create_user_id,modify_user_id,gmt_create,gmt_modified FROM article WHERE id=?"
	fqnOfLoggerClass = "org.apache.ibatis.logging.slf4j.Slf4jImpl"
	threadName = null
	loggerName = "com.wordplay.xiaozuo.mapper.ArticleMapper.selectById"
	loggerContext = {LoggerContext@9830} "ch.qos.logback.classic.LoggerContext[nacos]"
	loggerContextVO = {LoggerContextVO@9841} "LoggerContextVO{name='nacos', propertyMap={}, birthTime=1658994353149}"
	level = {Level@11773} "DEBUG"
	message = "==>  Preparing: SELECT id,user_id,category_id,title,thumb_img_url,brief,content,editor_type,view_num,like_num,comment_num,is_allow_comment,status,create_user_id,modify_user_id,gmt_create,gmt_modified FROM article WHERE id=?"
	formattedMessage = "==>  Preparing: SELECT id,user_id,category_id,title,thumb_img_url,brief,content,editor_type,view_num,like_num,comment_num,is_allow_comment,status,create_user_id,modify_user_id,gmt_create,gmt_modified FROM article WHERE id=?"
	argumentArray = null
	throwableProxy = null
	callerDataArray = null
	marker = {BasicMarker@11775} "MYBATIS"
	mdcPropertyMap = null
	timeStamp = 1658994751801
	* */
}
