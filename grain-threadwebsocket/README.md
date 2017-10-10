# grain-threadwebsocket

grain-threadwebsocket 引入这个库的web项目可以创建websocket服务器（基于系统多线程模型），可以更便捷的处理业务。


此项目依赖

	grain-log
	grain-msg
	grain-thread
	grain-threadmsg
	grain-websocket-lib
	commons-beanutils-1.9.3.jar
	commons-collections-3.2.2.jar
	commons-lang-2.6.jar
	commons-logging-1.2.jar
	ezmorph-1.0.6.jar
	javax.websocket-api-1.1.jar
	json-lib-2.4-jdk15.jar
	protobuf-java-3.1.0.jar
	protobuf-java-format-1.4.jar

使用

1、创建一个web工程，并创建一个继承HttpServlet的Servlet

InitServlet------继承Servlet，启动时初始化webscoket一些信息

	package init;
	import javax.servlet.ServletException;
	import javax.servlet.http.HttpServlet;
	import org.grain.msg.MsgManager;
	import org.grain.thread.AsyncThreadManager;
	import org.grain.threadwebsocket.ThreadWSManager;
	import org.grain.websokcetlib.WSManager;
	import org.slf4j.LoggerFactory;
	import protobuf.ws.Test.TestC;
	import protobuf.ws.Test.TestS;
	import test.GrainLog;
	import test.TestWSService;
	public class InitServlet extends HttpServlet {
		private static final long serialVersionUID = 1L;
		@Override
		public void init() throws ServletException {
			super.init();
			try {
				GrainLog grainLog = new GrainLog(LoggerFactory.getLogger("minaLog"));
				GrainLog grainLog1 = new GrainLog(LoggerFactory.getLogger("msgLog"));
				// 初始化线程消息
				AsyncThreadManager.init(100, 10, 3, 0, grainLog1);
				AsyncThreadManager.start();
				MsgManager.init(true, grainLog1);
				WSManager.init(grainLog1);
				// 映射操作码解析类,如果不设置线程优先级则随机线程优先级
				ThreadWSManager.addThreadMapping("testc", TestC.class, new int[] { 2, 1 });
				ThreadWSManager.addThreadMapping("tests", TestS.class, null);
				// 注册tcp回调函数
				TestWSService testWSService = new TestWSService();
				WSManager.addWSListener(testWSService);
			} catch (Exception e) {
			}
		}
	}

	
TestWSService-------实现	IWSListener接口的类型，注册到WSManager即可，关注testc这个操作码的消息包
	
	package test;
	import java.io.IOException;
	import java.util.HashMap;
	import java.util.Map;
	import javax.websocket.EncodeException;
	import javax.websocket.Session;
	import org.grain.websokcetlib.IWSListener;
	import org.grain.websokcetlib.WsPacket;
	import protobuf.ws.Test.TestC;
	import protobuf.ws.Test.TestS;
	public class TestWSService implements IWSListener {
		@Override
		public Map<String, String> getWSs() throws Exception {
			HashMap<String, String> map = new HashMap<>();
			map.put("testc", "onTestC");
			return map;
		}
		public void onTestC(WsPacket wsPacket) throws IOException, EncodeException {
			TestC testc = (TestC) wsPacket.getData();
			wsPacket.putMonitor("接到客户端发来的消息：" + testc.getMsg());
			TestS.Builder tests = TestS.newBuilder();
			tests.setWsOpCode("tests");
			tests.setMsg("你好客户端，我是服务器");
			WsPacket pt = new WsPacket("tests", tests.build());
			Session session = (Session) wsPacket.session;
			session.getBasicRemote().sendObject(pt);
		}
	}
	
配置web.xml设置InitServlet启动


	<?xml version="1.0" encoding="UTF-8"?>
	<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://xmlns.jcp.org/xml/ns/javaee" xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd" id="WebApp_ID" version="3.1">
	  <display-name>grain-websocket-test</display-name>
	  <servlet>
	    <servlet-name>InitServlet</servlet-name>
	    <servlet-class>init.InitServlet</servlet-class>
	    <load-on-startup>0</load-on-startup>
	  </servlet>
	  <welcome-file-list>
	    <welcome-file>index.html</welcome-file>
	    <welcome-file>index.htm</welcome-file>
	    <welcome-file>index.jsp</welcome-file>
	    <welcome-file>default.html</welcome-file>
	    <welcome-file>default.htm</welcome-file>
	    <welcome-file>default.jsp</welcome-file>
	  </welcome-file-list>
	</web-app>
	
2、javascript客户端可以通过以下url进行websocket通讯

	ws://localhost:8080/项目名/ws
	