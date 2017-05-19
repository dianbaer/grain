package websocket;

import java.util.HashMap;
import java.util.Map;

import javax.websocket.Session;

import msg.IMsgListener;
import msg.MsgOpCode;
import msg.MsgPacket;

public class WebSocketServerService implements IMsgListener {
	private HashMap<String, Session> sessionClientMap = new HashMap<>();

	@Override
	public Map<String, String> getMsgs() throws Exception {
		HashMap<String, String> map = new HashMap<>();
		map.put(MsgOpCode.WEBSOCKET_CLIENT_CREATE_CONNECT, "websocketClientCreateConnect");
		map.put(MsgOpCode.WEBSOCKET_CLIENT_DISCONNECT, "websocketClientDisConnect");
		return map;
	}

	@Override
	public Object getInstance() {

		return this;
	}

	public void websocketClientCreateConnect(MsgPacket msgPacket) {
		Session session = (Session) msgPacket.getOtherData();
		sessionClientMap.put(session.getId(), session);
	}

	public void websocketClientDisConnect(MsgPacket msgPacket) {
		Session session = (Session) msgPacket.getOtherData();
		sessionClientMap.remove(session.getId());
	}

}
