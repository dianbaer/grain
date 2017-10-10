# grain-tcp

grain-tcp 可以创建tcp客户端与服务器，进行长链接通讯。


此项目依赖

	grain-log
	grain-msg
	mina-core-2.0.16.jar
	protobuf-java-3.1.0.jar
	slf4j-api-1.7.22.jar

使用

一、创建TCP客户端，例子：grain-tcp-clienttest

TestMsgService-----实现IMsgListener接口的类，关注TcpMsg.MINA_SERVER_CONNECTED连接到tcp服务器成功的消息，并推送tcp消息至服务器

	package test;
	import java.util.HashMap;
	import java.util.Map;
	import org.apache.mina.core.session.IoSession;
	import org.grain.msg.IMsgListener;
	import org.grain.msg.MsgPacket;
	import org.grain.tcp.TcpMsg;
	import org.grain.tcp.TcpPacket;
	import protobuf.tcp.Test.TestC;
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
			TestC.Builder builder = TestC.newBuilder();
			builder.setName("你好啊");
			TcpPacket pt = new TcpPacket(TestTCode.TESTC, builder.build());
			session.write(pt);
		}
	}


TestTCode-----操作码

	package test;
	public class TestTCode {
		public static int TESTC = 1;
		public static int TESTS = 2;
	}
	
TestTcpServiceC-----实现	ITcpListener的类，关注tcp服务器返回TestTCode.TESTS消息
	
	package test;
	import java.util.HashMap;
	import java.util.Map;
	import org.grain.tcp.ITcpListener;
	import org.grain.tcp.TcpPacket;
	import protobuf.tcp.Test.TestS;
	public class TestTcpServiceC implements ITcpListener {
		@Override
		public Map<Integer, String> getTcps() throws Exception {
			HashMap<Integer, String> map = new HashMap<>();
			map.put(TestTCode.TESTS, "onTestS");
			return map;
		}
		public void onTestS(TcpPacket tcpPacket) {
			tcpPacket.putMonitor("接到客户端发来的消息");
			TestS tests = (TestS) tcpPacket.getData();
			tcpPacket.putMonitor("发来名字为：" + tests.getName());
		}
	}


初始化tcp客户端

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
	
	

二、创建TCP服务器，例子：grain-tcp-servertest

TestTCode-----操作码

	package test;
	public class TestTCode {
		public static int TESTC = 1;
		public static int TESTS = 2;
	}

TestTcpServiceS----实现ITcpListener接口的类，关注客户端发来TestTCode.TESTC消息

	package test;
	import java.util.HashMap;
	import java.util.Map;
	import org.apache.mina.core.session.IoSession;
	import org.grain.tcp.ITcpListener;
	import org.grain.tcp.TcpPacket;
	import protobuf.tcp.Test.TestC;
	import protobuf.tcp.Test.TestS;
	public class TestTcpServiceS implements ITcpListener {
		@Override
		public Map<Integer, String> getTcps() throws Exception {
			HashMap<Integer, String> map = new HashMap<>();
			map.put(TestTCode.TESTC, "onTestC");
			return map;
		}
		public void onTestC(TcpPacket tcpPacket) {
			tcpPacket.putMonitor("接到客户端发来的消息");
			TestC testc = (TestC) tcpPacket.getData();
			tcpPacket.putMonitor("发来名字为：" + testc.getName());
			TestS.Builder builder = TestS.newBuilder();
			builder.setName("客户端你好");
			TcpPacket pt = new TcpPacket(TestTCode.TESTS, builder.build());
			((IoSession) tcpPacket.session).write(pt);
		}
	}

初始化TCP服务器

	// 初始化消息
	MsgManager.init(true, null);
	// 映射操作码解析类TestC.class对应的protobuf解析类
	TcpManager.addMapping(TestTCode.TESTC, TestC.class);
	TcpManager.addMapping(TestTCode.TESTS, TestS.class);
	// 注册tcp回调函数
	TestTcpServiceS testTcpServiceS = new TestTcpServiceS();
	TcpManager.addTcpListener(testTcpServiceS);
	// 创建TCP服务器
	MinaServer.init("0.0.0.0", 7005, MinaServerHandler.class, true, null);
	
	
	
