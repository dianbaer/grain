package org.grain.tcp;

import java.util.Map;

public interface ITcpListener {
	/**
	 * 关注的tcp消息类型，实现此接口，tcp分发器会回调这些注册的函数
	 * 
	 * @return Map<Integer, String> tcp操作码，处理函数名
	 * @throws Exception
	 */
	public Map<Integer, String> getTcps() throws Exception;
}
