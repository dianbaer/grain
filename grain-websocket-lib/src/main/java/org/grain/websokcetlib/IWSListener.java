package org.grain.websokcetlib;

import java.util.Map;

public interface IWSListener {
	/**
	 * 实现此接口，可以进行webscoket关注消息注册，并在接到消息时回调
	 * 
	 * @return Map<String, String> 操作码，回调函数名
	 * @throws Exception
	 */
	public Map<String, String> getWSs() throws Exception;
}
