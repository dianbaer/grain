package mina;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

import msg.IMsgListener;
import msg.MsgManager;
import msg.MsgOpCode;
import msg.MsgPacket;
import protobuf.msg.MinaMsg.MinaServerCanUse;
import protobuf.msg.MinaMsg.MinaServerConnected;
import protobuf.msg.MinaMsg.MinaServerDisConnect;

public class MinaClientService implements IMsgListener {
	private HashMap<String, IoSession> ioSessionServerMap = new HashMap<String, IoSession>();

	@Override
	public Map<String, String> getMsgs() throws Exception {
		HashMap<String, String> map = new HashMap<>();
		map.put(MsgOpCode.MINA_SERVER_CONNECTED, "minaServerConnected");
		map.put(MsgOpCode.MINA_SERVER_DISCONNECT, "minaServerDisConnect");
		return map;
	}

	@Override
	public Object getInstance() {

		return this;
	}

	public void minaServerConnected(MsgPacket msgPacket) {
		IoSession ioSession = (IoSession) msgPacket.getOtherData();
		MinaServerConnected message = (MinaServerConnected) msgPacket.getData();
		String name = message.getName();
		ioSessionServerMap.put(name, ioSession);

		MinaServerCanUse.Builder builder = MinaServerCanUse.newBuilder();
		builder.setName(name);
		MsgPacket sendMsgPacket = new MsgPacket(MsgOpCode.MINA_SERVER_CAN_USE, builder.build(), MsgManager.USE_MSG_MONITOR);
		MsgManager.dispatchMsg(sendMsgPacket);
	}

	public void minaServerDisConnect(MsgPacket msgPacket) {
		MinaServerDisConnect message = (MinaServerDisConnect) msgPacket.getData();
		String name = message.getName();
		ioSessionServerMap.remove(name);
	}

	public IoSession getServerIoSession(String name) {
		return ioSessionServerMap.get(name);
	}

}
