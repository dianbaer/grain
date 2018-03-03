package test;

import org.grain.msg.MsgManager;
import org.grain.rpc.ThreadMinaClientHandler;
import org.grain.rpc.ThreadTcpManager;
import org.grain.tcp.MinaClient;
import org.grain.tcp.TcpManager;
import org.grain.tcp.TcpMsg;
import org.grain.rpc.WaitLockManager;
import org.grain.thread.AsyncThreadManager;
import org.grain.threadmsg.ThreadMsgManager;
import org.slf4j.LoggerFactory;

import protobuf.tcp.Test.RPCTestC;
import protobuf.tcp.Test.RPCTestS;

public class RPCClientTest {

	public static void main(String[] args) throws Exception {
		GrainLog grainLog = new GrainLog(LoggerFactory.getLogger("minaLog"));
		GrainLog grainLog1 = new GrainLog(LoggerFactory.getLogger("msgLog"));
		// 初始化线程消息
		AsyncThreadManager.init(100, 10, 3, 1, grainLog1);
		AsyncThreadManager.start();
		MsgManager.init(true, grainLog1);
		// 设置消息归属线程，不设置则随机分配
		ThreadMsgManager.addMapping(TcpMsg.MINA_SERVER_CONNECTED, new int[] { 1, 1 });
		ThreadMsgManager.addMapping(TcpMsg.MINA_SERVER_DISCONNECT, new int[] { 1, 1 });
		// 注册关注的消息
		TestMsgService testService = new TestMsgService();
		MsgManager.addMsgListener(testService);
		// 映射操作码解析类
		ThreadTcpManager.addThreadMapping(TestTCode.TEST_RPC_C, RPCTestC.class, null);
		ThreadTcpManager.addThreadMapping(TestTCode.TEST_RPC_S, RPCTestS.class, null);
		ThreadTcpManager.addThreadMapping(TestTCode.TEST_RPC_SERVER, RPCTestS.class, null);
		ThreadTcpManager.addThreadMapping(TestTCode.TEST_RPC_CLIENT, RPCTestC.class, null);
		
		TestRPCServiceC testRPCServiceC = new TestRPCServiceC();
		TcpManager.addTcpListener(testRPCServiceC);
		WaitLockManager.init(120000);
		ThreadTcpManager.init();
		// 创建TCP客户端
		MinaClient.init(new String[] { "0.0.0.0" }, new int[] { 7005 }, new String[] { "testserver" }, ThreadMinaClientHandler.class, 10, true, grainLog);

	}

}
