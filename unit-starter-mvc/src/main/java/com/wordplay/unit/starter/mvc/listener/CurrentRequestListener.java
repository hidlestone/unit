package com.wordplay.unit.starter.mvc.listener;

import com.wordplay.unit.starter.core.context.CurrentContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

/**
 * webapp处理HTTP请求时创建/使用的线程局部变量，
 * 那么避免线程局部泄漏的一种方法是使用webapp的ServletContext注册ServletRequestListener，
 * 并实现监听器的requestDestroyed方法来清除线程当前线程的本地化。
 * <p>
 * 防止内存泄漏
 *
 * @author zhuangpf
 */
//@Component
public class CurrentRequestListener implements ServletRequestListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(CurrentRequestListener.class);
	
	@Override
	public void requestDestroyed(ServletRequestEvent sre) {
		// 清空本地线程中存储的变量，避免内存泄漏
		CurrentContextHelper.cleanup();
		LOGGER.debug("执行线程清理...");
	}

}
