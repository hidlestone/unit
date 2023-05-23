package com.wordplay.unit.starter.mvc.filter;

import com.wordplay.unit.starter.core.context.CurrentContextHelper;

import javax.servlet.http.HttpServletResponse;

/**
 * @author zhuangpf
 * @date 2023-05-22
 */
public class HeaderWriterResponseWrapper extends OnCommittedResponseWrapper {

	HeaderWriterResponseWrapper(HttpServletResponse response) {
		super(response);
	}

	protected void onResponseCommitted() {
		this.writeHeaders();
		this.disableOnResponseCommitted();
	}

	protected void writeHeaders() {
		if (!this.isDisableOnResponseCommitted()) {
			this.getHttpResponse().addHeader("Access-Control-Expose-Headers", "Aes-Key");
			this.getHttpResponse().addHeader("Aes-Key", CurrentContextHelper.get().getResponseAesKey());
		}
	}

	private HttpServletResponse getHttpResponse() {
		return (HttpServletResponse) this.getResponse();
	}
	
}
