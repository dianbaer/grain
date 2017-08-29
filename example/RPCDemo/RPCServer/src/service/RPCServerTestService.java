package service;

import java.util.HashMap;
import java.util.Map;

import log.LogManager;
import mina.ITcpListener;
import protobuf.tcp.TestOuterClass.Test;
import tcp.TOpCodeRPCServer;
import tcp.TcpPacket;

public class RPCServerTestService implements ITcpListener {

	@Override
	public Map<Integer, String> getTcps() throws Exception {
		HashMap<Integer, String> map = new HashMap<>();
		map.put(TOpCodeRPCServer.TEST, "testHandle");
		return map;
	}

	@Override
	public Object getInstance() {
		return this;
	}

	public TcpPacket testHandle(TcpPacket tcpPacket) {
		Test message = (Test) tcpPacket.getData();
		LogManager.minaLog.info("接到RPC请求：" + message.getName());
		Test.Builder builder = Test.newBuilder();
		builder.setName("hello RPCClient，im RPCSERVER");
		TcpPacket pt = new TcpPacket(TOpCodeRPCServer.TEST, builder.build());
		LogManager.minaLog.info("返回RPC结果");
		return pt;
	}
}
