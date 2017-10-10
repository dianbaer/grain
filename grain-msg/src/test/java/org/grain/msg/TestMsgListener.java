package org.grain.msg;

import java.util.HashMap;
import java.util.Map;

public class TestMsgListener implements IMsgListener {

	@Override
	public Map<String, String> getMsgs() throws Exception {
		HashMap<String, String> map = new HashMap<>();
		map.put("createuser", "createUserHandle");
		return map;
	}

	public void createUserHandle(MsgPacket msgPacket) {
		System.out.println("接到消息：" + msgPacket.getMsgOpCode());
	}
}
