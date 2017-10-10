package org.grain.distributedlock;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.grain.msg.IMsgListener;
import org.grain.msg.MsgPacket;
import org.grain.tcp.TcpMsg;
import org.grain.threadmsg.ThreadMsgManager;

public class MinaClientService implements IMsgListener {
	private HashMap<String, IoSession> ioSessionServerMap = new HashMap<String, IoSession>();

	@Override
	public Map<String, String> getMsgs() throws Exception {
		HashMap<String, String> map = new HashMap<>();
		map.put(TcpMsg.MINA_SERVER_CONNECTED, "minaServerConnected");
		map.put(TcpMsg.MINA_SERVER_DISCONNECT, "minaServerDisConnect");
		return map;
	}

	/**
	 * 通知叫name的锁服务器可以使用了
	 * 
	 * @param msgPacket
	 */
	public void minaServerConnected(MsgPacket msgPacket) {
		IoSession ioSession = (IoSession) msgPacket.getOtherData();
		String name = (String) msgPacket.getData();
		ioSessionServerMap.put(name, ioSession);
		ThreadMsgManager.dispatchThreadMsg(DistributedlockMsg.DISTRIBUTEDLOCK_SERVER_CAN_USE, name, null);
	}

	public void minaServerDisConnect(MsgPacket msgPacket) {
		String name = (String) msgPacket.getData();
		ioSessionServerMap.remove(name);
	}

	public IoSession getServerIoSession(String name) {
		return ioSessionServerMap.get(name);
	}

}
