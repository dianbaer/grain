package org.grain.msg;

import java.util.Map;

public interface IMsgListener {
	/**
	 * 实现此接口可以关注消息，消息分发器会回调消息处理函数
	 * 
	 * @return Map<String, String> 消息类型，消息处理函数
	 * @throws Exception
	 */
	public Map<String, String> getMsgs() throws Exception;
}
