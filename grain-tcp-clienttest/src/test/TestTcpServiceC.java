package test;

import java.util.HashMap;
import java.util.Map;

import org.grain.tcp.ITcpListener;
import org.grain.tcp.TcpPacket;

import protobuf.tcp.Test.TestS;

public class TestTcpServiceC implements ITcpListener {

	@Override
	public Map<Integer, String> getTcps() throws Exception {
		HashMap<Integer, String> map = new HashMap<>();
		map.put(TestTCode.TESTS, "onTestS");
		return map;
	}

	public void onTestS(TcpPacket tcpPacket) {
		tcpPacket.putMonitor("接到客户端发来的消息");
		TestS tests = (TestS) tcpPacket.getData();
		tcpPacket.putMonitor("发来名字为：" + tests.getName());
	}
}
