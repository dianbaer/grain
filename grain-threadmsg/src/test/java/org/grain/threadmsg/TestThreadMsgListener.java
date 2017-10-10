package org.grain.threadmsg;

import java.util.HashMap;
import java.util.Map;

import org.grain.msg.IMsgListener;
import org.grain.msg.MsgPacket;

public class TestThreadMsgListener implements IMsgListener {

	@Override
	public Map<String, String> getMsgs() throws Exception {
		HashMap<String, String> map = new HashMap<>();
		map.put("createuser", "createUserHandle");
		map.put("updateuser", "updateUserHandle");
		return map;
	}

	public void createUserHandle(MsgPacket msgPacket) {
		System.out.println("createUserHandle接到消息：" + msgPacket.getMsgOpCode());
	}

	public void updateUserHandle(MsgPacket msgPacket) {
		System.out.println("updateUserHandle接到消息：" + msgPacket.getMsgOpCode());
	}

}
