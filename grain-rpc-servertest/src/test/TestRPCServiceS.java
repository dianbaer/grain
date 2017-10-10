package test;

import java.util.HashMap;
import java.util.Map;

import org.grain.tcp.ITcpListener;
import org.grain.tcp.TcpPacket;

import protobuf.tcp.Test.RPCTestC;
import protobuf.tcp.Test.RPCTestS;

public class TestRPCServiceS implements ITcpListener {

	@Override
	public Map<Integer, String> getTcps() throws Exception {
		HashMap<Integer, String> map = new HashMap<>();
		map.put(TestTCode.TEST_RPC_C, "onTestRPCC");
		return map;
	}

	public TcpPacket onTestRPCC(TcpPacket tcpPacket) {
		tcpPacket.putMonitor("接到客户端发来的消息");
		RPCTestC testc = (RPCTestC) tcpPacket.getData();
		tcpPacket.putMonitor("发来名字为：" + testc.getName());
		RPCTestS.Builder builder = RPCTestS.newBuilder();
		builder.setName("客户端你好");
		TcpPacket pt = new TcpPacket(TestTCode.TEST_RPC_S, builder.build());
		return pt;
	}
}
