package service;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

import init.Init;
import log.LogManager;
import mina.MinaClientService;
import mina.waitlock.WaitLockManager;
import msg.IMsgListener;
import msg.MsgOpCode;
import msg.MsgPacket;
import protobuf.msg.MinaMsg.MinaServerCanUse;
import protobuf.tcp.Test1OuterClass.Test1;
import protobuf.tcp.TestOuterClass.Test;
import server.MinaServerName;
import tcp.TOpCodeRPCServer;
import tcp.TOpCodeRPCServer1;
import tcp.TcpPacket;

public class RPCClientTestService implements IMsgListener {

	@Override
	public Map<String, String> getMsgs() throws Exception {
		HashMap<String, String> map = new HashMap<>();
		map.put(MsgOpCode.MINA_SERVER_CAN_USE, "serverCanUse");
		return map;
	}

	@Override
	public Object getInstance() {
		return this;
	}

	public void serverCanUse(MsgPacket msgPacket) {
		MinaServerCanUse message = (MinaServerCanUse) msgPacket.getData();
		if (message.getName().equals(MinaServerName.RPC_SERVER)) {
			MinaClientService minaClientService = (MinaClientService) Init.getService(MinaClientService.class);
			IoSession ioSession = minaClientService.getServerIoSession(message.getName());
			Test.Builder builder = Test.newBuilder();
			builder.setName("hello RPCServer");
			TcpPacket pt = new TcpPacket(TOpCodeRPCServer.TEST, builder.build());
			LogManager.minaLog.info("发送rpc请求");
			TcpPacket returnPt = WaitLockManager.lock(ioSession, pt);
			Test message1 = (Test) returnPt.getData();
			LogManager.minaLog.info("成功返回rpc结果：" + message1.getName());
		} else if (message.getName().equals(MinaServerName.RPC_SERVER1)) {
			MinaClientService minaClientService = (MinaClientService) Init.getService(MinaClientService.class);
			IoSession ioSession = minaClientService.getServerIoSession(message.getName());
			Test1.Builder builder = Test1.newBuilder();
			builder.setName("hello RPCServer1");
			TcpPacket pt = new TcpPacket(TOpCodeRPCServer1.TEST1, builder.build());
			LogManager.minaLog.info("发送rpc请求");
			TcpPacket returnPt = WaitLockManager.lock(ioSession, pt);
			Test1 message1 = (Test1) returnPt.getData();
			LogManager.minaLog.info("成功返回rpc结果：" + message1.getName());
		}
	}
}
