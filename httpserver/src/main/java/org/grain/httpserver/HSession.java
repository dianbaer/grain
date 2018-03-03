package org.grain.httpserver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HSession {
	public HttpServletRequest request;
	public HttpServletResponse response;
	// 用于存其他数据，扩展用的
	public Object otherData;
	public HeadParam headParam;

	public void clear() {
		request = null;
		response = null;
		otherData = null;
		headParam.clear();
		headParam = null;
	}
}
