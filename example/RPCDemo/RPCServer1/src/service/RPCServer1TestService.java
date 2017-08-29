package service;

import java.util.HashMap;
import java.util.Map;

import log.LogManager;
import mina.ITcpListener;
import protobuf.tcp.Test1OuterClass.Test1;
import tcp.TOpCodeRPCServer1;
import tcp.TcpPacket;

public class RPCServer1TestService implements ITcpListener {

	@Override
	public Map<Integer, String> getTcps() throws Exception {
		HashMap<Integer, String> map = new HashMap<>();
		map.put(TOpCodeRPCServer1.TEST1, "testHandle");
		return map;
	}

	@Override
	public Object getInstance() {
		return this;
	}

	public TcpPacket testHandle(TcpPacket tcpPacket) {
		Test1 message = (Test1) tcpPacket.getData();
		LogManager.minaLog.info("接到RPC请求：" + message.getName());
		Test1.Builder builder = Test1.newBuilder();
		builder.setName("hello RPCClient");
		TcpPacket pt = new TcpPacket(TOpCodeRPCServer1.TEST1, builder.build());
		LogManager.minaLog.info("返回RPC结果");
		return pt;
	}
}
