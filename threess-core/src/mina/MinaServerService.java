package mina;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

import msg.IMsgListener;
import msg.MsgOpCode;
import msg.MsgPacket;

public class MinaServerService implements IMsgListener {
	private HashMap<Long, IoSession> ioSessionClientMap = new HashMap<>();

	@Override
	public Map<String, String> getMsgs() throws Exception {
		HashMap<String, String> map = new HashMap<>();
		map.put(MsgOpCode.MINA_CLIENT_CREATE_CONNECT, "minaClientCreateConnect");
		map.put(MsgOpCode.MINA_CLIENT_DISCONNECT, "minaClientDisConnect");
		return map;
	}

	@Override
	public Object getInstance() {

		return this;
	}

	public void minaClientCreateConnect(MsgPacket msgPacket) {
		IoSession ioSession = (IoSession) msgPacket.getOtherData();
		ioSessionClientMap.put(ioSession.getId(), ioSession);
	}

	public void minaClientDisConnect(MsgPacket msgPacket) {
		IoSession ioSession = (IoSession) msgPacket.getOtherData();
		ioSessionClientMap.remove(ioSession.getId());
	}

}
