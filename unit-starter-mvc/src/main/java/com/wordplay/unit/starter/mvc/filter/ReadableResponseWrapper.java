package com.wordplay.unit.starter.mvc.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhuangpf
 * @date 2023-05-22
 */
public class ReadableResponseWrapper extends HttpServletResponseWrapper {

	public static final List<String> SUPPORTED_CONTENT_TYPES = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;

		{
			this.add("application/json;charset=UTF-8");
			this.add("application/json");
			this.add("text/plain");
			this.add("text/plain;charset=UTF-8");
			this.add("text/xml");
			this.add("text/xml;charset=UTF-8");
		}
	};
	final ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public ReadableResponseWrapper(HttpServletResponse response) {
		super(response);
	}

	public ServletOutputStream getOutputStream() throws IOException {
		final OutputStream out = super.getOutputStream();
		return new ServletOutputStream() {
			public void write(int b) throws IOException {
				ReadableResponseWrapper.this.baos.write(b);
				out.write(b);
			}

			public boolean isReady() {
				return true;
			}

			public void setWriteListener(WriteListener listener) {
			}
		};
	}

	public PrintWriter getWriter() throws IOException {
		return super.getWriter();
	}

	public String contentToString() throws UnsupportedEncodingException {
		if (!SUPPORTED_CONTENT_TYPES.contains(this.getContentType())) {
			return "Content-Type [" + this.getContentType() + "] is not supported.";
		} else {
			return this.baos.size() > 102400 ? "Content-Length is to large." : this.baos.toString("UTF-8");
		}
	}

	public void reset() {
		super.reset();
		this.baos.reset();
	}

}
