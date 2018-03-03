package org.grain.httpserver;

import java.util.Map;

public interface IHttpListener {
	/**
	 * 实现此接口可以注册http对应消息处理函数
	 * 
	 * @return
	 */
	public Map<String, String> getHttps();
}
