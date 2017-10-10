# grain-distributedlock

grain-distributedlock 去中心化思路，支持锁客户端与锁服务器多对多的关系，支持锁类型的单键值。不同类型互不影响，相同类型不同键值互不影响。

此项目依赖

	grain-log
	grain-msg
	grain-tcp
	grain-thread
	grain-threadmsg
	grain-rpc
	mina-core-2.0.16.jar
	protobuf-java-3.1.0.jar
	slf4j-api-1.7.22.jar

使用

1、创建锁服务器

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

2、创建锁客户端

TestMsgService----实现IMsgListener接口的类，关注DistributedlockMsg.DISTRIBUTEDLOCK_SERVER_CAN_USE锁服务器准备完成的消息，然后发布消息多个线程调用分布式锁

	package test;
	import java.util.HashMap;
	import java.util.Map;
	import org.grain.distributedlock.DistributedLockClient;
	import org.grain.distributedlock.DistributedlockMsg;
	import org.grain.msg.IMsgListener;
	import org.grain.msg.MsgPacket;
	import org.grain.threadmsg.ThreadMsgManager;
	public class TestMsgService implements IMsgListener {
		@Override
		public Map<String, String> getMsgs() throws Exception {
			HashMap<String, String> map = new HashMap<>();
			map.put(DistributedlockMsg.DISTRIBUTEDLOCK_SERVER_CAN_USE, "onDistributedlockCanUse");
			map.put("DISTRIBUTEDLOCK_TEST", "onDistributedlockTest");
			map.put("DISTRIBUTEDLOCK_TEST1", "onDistributedlockTest1");
			return map;
		}
		public void onDistributedlockCanUse(MsgPacket msgPacket) {
			// 发布四条消息，分配至随机线程，不同类型互补影响，相同类型不同键值互不影响
			ThreadMsgManager.dispatchThreadMsg("DISTRIBUTEDLOCK_TEST", null, null);
			ThreadMsgManager.dispatchThreadMsg("DISTRIBUTEDLOCK_TEST", null, null);
			ThreadMsgManager.dispatchThreadMsg("DISTRIBUTEDLOCK_TEST1", null, null);
			ThreadMsgManager.dispatchThreadMsg("DISTRIBUTEDLOCK_TEST1", null, null);
		}
		public void onDistributedlockTest(MsgPacket msgPacket) {
			// 获取锁
			int lockId = DistributedLockClient.getLock("111", "user");
			if (lockId == 0) {
				return;
			}
			/*********** 执行分布式锁业务逻辑 *********/
			System.out.println("分布式锁id为：" + lockId);
			/*********** 执行分布式锁业务逻辑 *********/
			// 释放锁
			DistributedLockClient.unLock("111", "user", lockId);
		}
		public void onDistributedlockTest1(MsgPacket msgPacket) {
			// 获取锁
			int lockId = DistributedLockClient.getLock("222", "user");
			if (lockId == 0) {
				return;
			}
			/*********** 执行分布式锁业务逻辑 *********/
			System.out.println("分布式锁id为：" + lockId);
			/*********** 执行分布式锁业务逻辑 *********/
			// 释放锁
			DistributedLockClient.unLock("222", "user", lockId);
		}
	}
	
初始化锁客户端

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