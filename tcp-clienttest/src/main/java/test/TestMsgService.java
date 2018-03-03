package test;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.grain.msg.IMsgListener;
import org.grain.msg.MsgPacket;
import org.grain.tcp.TcpMsg;
import org.grain.tcp.TcpPacket;

import protobuf.tcp.Test.TestC;

public class TestMsgService implements IMsgListener {

	@Override
	public Map<String, String> getMsgs() throws Exception {
		HashMap<String, String> map = new HashMap<>();
		map.put(TcpMsg.MINA_SERVER_CONNECTED, "onServerConnected");
		return map;
	}

	public void onServerConnected(MsgPacket msgPacket) {
		IoSession session = (IoSession) msgPacket.getOtherData();
		System.out.println("接到消息：" + msgPacket.getMsgOpCode());
		TestC.Builder builder = TestC.newBuilder();
		builder.setName("你好啊");
		TcpPacket pt = new TcpPacket(TestTCode.TESTC, builder.build());
		session.write(pt);
	}

}
