package test;

import org.grain.msg.MsgManager;
import org.grain.tcp.MinaServer;
import org.grain.tcp.MinaServerHandler;
import org.grain.tcp.TcpManager;
import org.slf4j.LoggerFactory;

import protobuf.tcp.Test.TestC;
import protobuf.tcp.Test.TestS;

public class TCPServerTest {
	public static void main(String[] args) throws Exception {
		GrainLog grainLog = new GrainLog(LoggerFactory.getLogger("minaLog"));
		GrainLog grainLog1 = new GrainLog(LoggerFactory.getLogger("msgLog"));

		// 初始化消息
		MsgManager.init(true, grainLog1);
		// 映射操作码解析类
		TcpManager.addMapping(TestTCode.TESTC, TestC.class);
		TcpManager.addMapping(TestTCode.TESTS, TestS.class);
		// 注册tcp回调函数
		TestTcpServiceS testTcpServiceS = new TestTcpServiceS();
		TcpManager.addTcpListener(testTcpServiceS);
		// 创建TCP服务器
		MinaServer.init("0.0.0.0", 7005, MinaServerHandler.class, true, grainLog);

	}

}
