package test;

import org.grain.msg.MsgManager;
import org.grain.rpc.ThreadMinaServerHandler;
import org.grain.rpc.ThreadTcpManager;
import org.grain.rpc.WaitLockManager;
import org.grain.tcp.MinaServer;
import org.grain.tcp.TcpManager;
import org.grain.tcp.TcpMsg;
import org.grain.thread.AsyncThreadManager;
import org.grain.threadmsg.ThreadMsgManager;
import org.slf4j.LoggerFactory;

import protobuf.tcp.Test.RPCTestC;
import protobuf.tcp.Test.RPCTestS;

public class RPCServerTest {

	public static void main(String[] args) throws Exception {
		GrainLog grainLog = new GrainLog(LoggerFactory.getLogger("minaLog"));
		GrainLog grainLog1 = new GrainLog(LoggerFactory.getLogger("msgLog"));

		// 初始化线程消息
		AsyncThreadManager.init(100, 10, 3, 1, grainLog1);
		AsyncThreadManager.start();
		MsgManager.init(true, grainLog1);
		// 设置消息归属线程，不设置则随机分配
		ThreadMsgManager.addMapping(TcpMsg.MINA_CLIENT_CREATE_CONNECT, new int[] { 1, 1 });
		ThreadMsgManager.addMapping(TcpMsg.MINA_CLIENT_DISCONNECT, new int[] { 1, 1 });
		// 注册关注的消息
		TestMsgService testService = new TestMsgService();
		MsgManager.addMsgListener(testService);
		// 映射操作码解析类
		ThreadTcpManager.addThreadMapping(TestTCode.TEST_RPC_C, RPCTestC.class, null);
		ThreadTcpManager.addThreadMapping(TestTCode.TEST_RPC_S, RPCTestS.class, null);
		ThreadTcpManager.addThreadMapping(TestTCode.TEST_RPC_SERVER, RPCTestS.class, null);
		ThreadTcpManager.addThreadMapping(TestTCode.TEST_RPC_CLIENT, RPCTestC.class, null);
		// 注册tcp回调函数
		TestRPCServiceS testRPCServiceS = new TestRPCServiceS();
		TcpManager.addTcpListener(testRPCServiceS);
		WaitLockManager.init(120000);
		ThreadTcpManager.init();
		// 创建TCP服务器
		MinaServer.init("0.0.0.0", 7005, ThreadMinaServerHandler.class, true, grainLog);
	}

}
