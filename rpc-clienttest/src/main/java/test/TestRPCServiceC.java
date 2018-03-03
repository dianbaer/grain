package test;

import java.util.HashMap;
import java.util.Map;

import org.grain.tcp.ITcpListener;
import org.grain.tcp.TcpPacket;

import protobuf.tcp.Test.RPCTestC;
import protobuf.tcp.Test.RPCTestS;

public class TestRPCServiceC implements ITcpListener {

	@Override
	public Map<Integer, String> getTcps() throws Exception {
		HashMap<Integer, String> map = new HashMap<>();
		map.put(TestTCode.TEST_RPC_SERVER, "onTestRPCServer");
		return map;
	}

	public TcpPacket onTestRPCServer(TcpPacket tcpPacket) {
		tcpPacket.putMonitor("接到服务器发来的消息");
		RPCTestS tests = (RPCTestS) tcpPacket.getData();
		tcpPacket.putMonitor("发来名字为：" + tests.getName());
		RPCTestC.Builder builder = RPCTestC.newBuilder();
		builder.setName("服务器你好");
		TcpPacket pt = new TcpPacket(TestTCode.TEST_RPC_CLIENT, builder.build());
		return pt;
	}
}
