package test;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.grain.tcp.ITcpListener;
import org.grain.tcp.TcpPacket;

import protobuf.tcp.Test.TestC;
import protobuf.tcp.Test.TestS;

public class TestTcpServiceS implements ITcpListener {

	@Override
	public Map<Integer, String> getTcps() throws Exception {
		HashMap<Integer, String> map = new HashMap<>();
		map.put(TestTCode.TESTC, "onTestC");
		return map;
	}

	public void onTestC(TcpPacket tcpPacket) {
		tcpPacket.putMonitor("接到客户端发来的消息");
		TestC testc = (TestC) tcpPacket.getData();
		tcpPacket.putMonitor("发来名字为：" + testc.getName());
		TestS.Builder builder = TestS.newBuilder();
		builder.setName("客户端你好");
		TcpPacket pt = new TcpPacket(TestTCode.TESTS, builder.build());
		((IoSession) tcpPacket.session).write(pt);
	}
}
