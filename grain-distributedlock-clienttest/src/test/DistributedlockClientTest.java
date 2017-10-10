package test;

import java.util.HashMap;
import java.util.Map;

import org.grain.distributedlock.DistributedLockClient;
import org.grain.distributedlock.DistributedlockMsg;
import org.grain.distributedlock.MinaClientService;
import org.grain.msg.MsgManager;
import org.grain.rpc.ThreadMinaClientHandler;
import org.grain.rpc.ThreadTcpManager;
import org.grain.rpc.WaitLockManager;
import org.grain.tcp.MinaClient;
import org.grain.tcp.TcpMsg;
import org.grain.thread.AsyncThreadManager;
import org.grain.threadmsg.ThreadMsgManager;
import org.slf4j.LoggerFactory;

public class DistributedlockClientTest {

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
		ThreadMsgManager.addMapping(DistributedlockMsg.DISTRIBUTEDLOCK_SERVER_CAN_USE, new int[] { 1, 1 });
		// 注册关注的消息
		MinaClientService minaClientService = new MinaClientService();
		MsgManager.addMsgListener(minaClientService);
		TestMsgService testMsgService = new TestMsgService();
		MsgManager.addMsgListener(testMsgService);

		WaitLockManager.init(120000);
		ThreadTcpManager.init();
		// 初始化分布式锁客户端
		Map<String, String> lockToServer = new HashMap<String, String>();
		lockToServer.put("user", "testserver");
		lockToServer.put("group", "testserver");
		DistributedLockClient.init(lockToServer, grainLog1);
		// 创建TCP客户端
		MinaClient.init(new String[] { "0.0.0.0" }, new int[] { 7005 }, new String[] { "testserver" }, ThreadMinaClientHandler.class, 10, true, grainLog);
	}

}
