package com.wordplay.unit.starter.mail.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wordplay.unit.starter.mail.entity.MailSenderConfig;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Map;
import java.util.Properties;

/**
 * 邮件工具类
 *
 * @author zhuangpf
 */
public class FallMailUtil {

	/**
	 * 根据配置构建sender
	 *
	 * @param config
	 * @return
	 */
	public static JavaMailSender constructMailSender(MailSenderConfig config) {
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setHost(config.getHost());
		sender.setPort(config.getPort());
		sender.setUsername(config.getUsername());
		sender.setPassword(config.getPassword());
		sender.setProtocol(config.getProtocol());
		sender.setDefaultEncoding(config.getDefaultEncoding());
		Properties properties = new Properties();
		JSONObject jsonObject = JSON.parseObject(config.getProperties());
		for (Map.Entry entry : jsonObject.entrySet()) {
			properties.setProperty(entry.getKey().toString(), entry.getValue().toString());
		}
		sender.setJavaMailProperties(properties);
		// 会话
//		Authenticator auth = new FallMailAuthentication(config.getUsername(), config.getPassword());
//		Session session = Session.getDefaultInstance(properties, auth);
//		MimeMessage msg = new MimeMessage(session);
//		sender.setSession(session);
		return sender;
	}
}
