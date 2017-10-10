package test;

import org.grain.msg.MsgManager;
import org.grain.tcp.MinaClient;
import org.grain.tcp.MinaClientHandler;
import org.grain.tcp.TcpManager;
import org.slf4j.LoggerFactory;

import protobuf.tcp.Test.TestC;
import protobuf.tcp.Test.TestS;

public class TCPClientTest {
	public static void main(String[] args) throws Exception {
		GrainLog grainLog = new GrainLog(LoggerFactory.getLogger("minaLog"));
		GrainLog grainLog1 = new GrainLog(LoggerFactory.getLogger("msgLog"));
		// 初始化消息
		MsgManager.init(true, grainLog1);
		// 注册关注的消息
		TestMsgService testService = new TestMsgService();
		MsgManager.addMsgListener(testService);
		// 映射操作码解析类
		TcpManager.addMapping(TestTCode.TESTC, TestC.class);
		TcpManager.addMapping(TestTCode.TESTS, TestS.class);
		// 注册tcp回调函数
		TestTcpServiceC testTcpServiceC = new TestTcpServiceC();
		TcpManager.addTcpListener(testTcpServiceC);
		// 创建TCP客户端
		MinaClient.init(new String[] { "0.0.0.0" }, new int[] { 7005 }, new String[] { "testserver" }, MinaClientHandler.class, 10, true, grainLog);

	}

}
