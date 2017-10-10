package test;

import java.util.ArrayList;

import org.grain.distributedlock.DistributedLockServer;
import org.grain.distributedlock.DistributedLockService;
import org.grain.distributedlock.DistributedlockMsg;
import org.grain.distributedlock.DistributedlockServerHandler;
import org.grain.distributedlock.MergeTCPService;
import org.grain.msg.MsgManager;
import org.grain.rpc.ThreadTcpManager;
import org.grain.rpc.WaitLockManager;
import org.grain.tcp.MinaServer;
import org.grain.tcp.TcpManager;
import org.grain.tcp.TcpMsg;
import org.grain.thread.AsyncThreadManager;
import org.grain.threadmsg.ThreadMsgManager;
import org.slf4j.LoggerFactory;

public class DistributedlockServerTest {

	public static void main(String[] args) throws Exception {
		GrainLog grainLog = new GrainLog(LoggerFactory.getLogger("minaLog"));
		GrainLog grainLog1 = new GrainLog(LoggerFactory.getLogger("msgLog"));
		// 初始化线程消息(需要锁定两条线程第一条唤醒用的，第二条获取锁信息汇集用的)
		AsyncThreadManager.init(100, 10, 3, 2, grainLog1);
		AsyncThreadManager.start();
		MsgManager.init(true, grainLog1);
		// 设置消息归属线程，不设置则随机分配
		ThreadMsgManager.addMapping(TcpMsg.MINA_CLIENT_CREATE_CONNECT, new int[] { 1, 1 });
		ThreadMsgManager.addMapping(TcpMsg.MINA_CLIENT_DISCONNECT, new int[] { 1, 1 });
		// 第二条用于汇集用
		int[] threadPriority = AsyncThreadManager.getLockThreadPriority(2);
		ThreadMsgManager.addMapping(DistributedlockMsg.MERGE_TCPPACKET, threadPriority);
		// 注册关注的消息
		MergeTCPService mergeTCPService = new MergeTCPService();
		MsgManager.addMsgListener(mergeTCPService);

		WaitLockManager.init(120000);
		ThreadTcpManager.init();
		// 初始化分布式锁服务器
		ArrayList<String> types = new ArrayList<>();
		types.add("user");
		types.add("group");
		DistributedLockServer.init(types, grainLog1);
		// 注册tcp回调函数
		DistributedLockService distributedLockService = new DistributedLockService();
		TcpManager.addTcpListener(distributedLockService);
		// 创建TCP服务器
		MinaServer.init("0.0.0.0", 7005, DistributedlockServerHandler.class, true, grainLog);
	}

}
