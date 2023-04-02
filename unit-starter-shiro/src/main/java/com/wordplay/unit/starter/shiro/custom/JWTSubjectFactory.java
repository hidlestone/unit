package com.wordplay.unit.starter.shiro.custom;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;

/**
 * 关闭 shiro session
 *
 * @author zhuangpf
 */
public class JWTSubjectFactory extends DefaultWebSubjectFactory {

	@Override
	public Subject createSubject(SubjectContext context) {
		context.setSessionCreationEnabled(false);
		return super.createSubject(context);
	}

}
