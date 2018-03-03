package org.grain.httpserver;

import java.util.HashMap;
import java.util.Map;

public class HeadParam {
	// 操作码
	public String hOpCode;
	// 身份
	public String token;
	// 请求唯一id
	public String fileUuid;
	// 消息包
	public String packet;
	// 头消息map
	public Map<String, String> headParam = new HashMap<>();
	// param与from解析出来的参数
	public Map<String, String> parameterParam = new HashMap<>();

	public HeadParam() {

	}

	public void clear() {
		headParam.clear();
		headParam = null;
		parameterParam.clear();
		parameterParam = null;
	}
}
