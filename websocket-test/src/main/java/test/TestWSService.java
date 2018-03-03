package test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.EncodeException;
import javax.websocket.Session;

import org.grain.websokcetlib.IWSListener;
import org.grain.websokcetlib.WsPacket;

import protobuf.ws.Test.TestC;
import protobuf.ws.Test.TestS;

public class TestWSService implements IWSListener {

	@Override
	public Map<String, String> getWSs() throws Exception {
		HashMap<String, String> map = new HashMap<>();
		map.put("testc", "onTestC");
		return map;
	}

	public void onTestC(WsPacket wsPacket) throws IOException, EncodeException {
		TestC testc = (TestC) wsPacket.getData();
		wsPacket.putMonitor("接到客户端发来的消息：" + testc.getMsg());
		TestS.Builder tests = TestS.newBuilder();
		tests.setWsOpCode("tests");
		tests.setMsg("你好客户端，我是服务器");
		WsPacket pt = new WsPacket("tests", tests.build());
		Session session = (Session) wsPacket.session;
		session.getBasicRemote().sendObject(pt);
	}

}
