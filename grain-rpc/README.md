# grain-rpc

grain-rpc 可以创建RPC客户端与服务器，进行远程对象访问


此项目依赖

	grain-log
	grain-msg
	grain-tcp
	grain-thread
	grain-threadmsg
	mina-core-2.0.16.jar
	protobuf-java-3.1.0.jar
	slf4j-api-1.7.22.jar
	
	
RPC客户端

![RPC客户端](./rpc-client.png "rpc-client.png")


RPC服务器


![RPC客户端](./rpc-server.png "rpc-server.png")	

	

使用

一、创建TCP客户端，例子：grain-rpc-clienttest

TestMsgService-----实现IMsgListener接口的类，关注TcpMsg.MINA_SERVER_CONNECTED连接到tcp服务器成功的消息，并调用RPC

	package test;
	import java.util.HashMap;
	import java.util.Map;
	import org.apache.mina.core.session.IoSession;
	import org.grain.msg.IMsgListener;
	import org.grain.msg.MsgPacket;
	import org.grain.tcp.TcpMsg;
	import org.grain.tcp.TcpPacket;
	import org.grain.tcp.rpc.WaitLockManager;
	import protobuf.tcp.Test.RPCTestC;
	import protobuf.tcp.Test.RPCTestS;
	public class TestMsgService implements IMsgListener {
		@Override
		public Map<String, String> getMsgs() throws Exception {
			HashMap<String, String> map = new HashMap<>();
			map.put(TcpMsg.MINA_SERVER_CONNECTED, "onServerConnected");
			return map;
		}
		public void onServerConnected(MsgPacket msgPacket) {
			IoSession session = (IoSession) msgPacket.getOtherData();
			System.out.println("接到消息：" + msgPacket.getMsgOpCode());
			RPCTestC.Builder builder = RPCTestC.newBuilder();
			builder.setName("你好啊");
			TcpPacket pt = new TcpPacket(TestTCode.TEST_RPC_C, builder.build());
			TcpPacket ptReturn = WaitLockManager.lock(session, pt);
			RPCTestS rpcTestS = (RPCTestS) ptReturn.getData();
			System.out.println("接到RPC消息：" + rpcTestS.getName());
		}
	}

TestTCode-----操作码

	package test;
	public class TestTCode {
		public static int TEST_RPC_C = 1;
		public static int TEST_RPC_S = 2;
	}

	



初始化RPC客户端

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
	WaitLockManager.init(120000);
	ThreadTcpManager.init();
	// 创建TCP客户端
	MinaClient.init(new String[] { "0.0.0.0" }, new int[] { 7005 }, new String[] { "testserver" }, ThreadMinaClientHandler.class, 10, true, grainLog);
	
	

二、创建RPC服务器，例子：grain-rpc-servertest

TestTCode-----操作码

	package test;
	public class TestTCode {
		public static int TEST_RPC_C = 1;
		public static int TEST_RPC_S = 2;
	}


TestRPCServiceS----实现ITcpListener接口的类，关注客户端发来TestTCode.TEST_RPC_C消息并返回TestTCode.TEST_RPC_S消息包

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


初始化RPC服务器

	// 初始化线程消息
	AsyncThreadManager.init(100, 10, 3, 1, grainLog1);
	AsyncThreadManager.start();
	MsgManager.init(true, grainLog1);
	// 设置消息归属线程，不设置则随机分配
	ThreadMsgManager.addMapping(TcpMsg.MINA_CLIENT_CREATE_CONNECT, new int[] { 1, 1 });
	ThreadMsgManager.addMapping(TcpMsg.MINA_CLIENT_DISCONNECT, new int[] { 1, 1 });
	// 映射操作码解析类
	ThreadTcpManager.addThreadMapping(TestTCode.TEST_RPC_C, RPCTestC.class, null);
	ThreadTcpManager.addThreadMapping(TestTCode.TEST_RPC_S, RPCTestS.class, null);
	// 注册tcp回调函数
	TestRPCServiceS testRPCServiceS = new TestRPCServiceS();
	TcpManager.addTcpListener(testRPCServiceS);
	WaitLockManager.init(120000);
	ThreadTcpManager.init();
	// 创建TCP服务器
	MinaServer.init("0.0.0.0", 7005, ThreadMinaServerHandler.class, true, grainLog);
	