package org.grain.distributedlock;

import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.grain.distributedlock.tcp.DistributedLock.DistributedLockC1;
import org.grain.distributedlock.tcp.DistributedLock.DistributedLockC2;
import org.grain.distributedlock.tcp.DistributedLock.DistributedLockS1;
import org.grain.log.ILog;
import org.grain.msg.MsgManager;
import org.grain.rpc.ThreadTcpManager;
import org.grain.rpc.WaitLockManager;
import org.grain.tcp.TcpPacket;

public class DistributedLockClient {
	private static Map<String, String> lockToServer;

	/**
	 * 初始化分布式锁客户端
	 * 
	 * @param lockToServer
	 *            锁类型与服务器名的映射表例如 user-userlockserver
	 * @param log
	 *            日志可以为null
	 * @throws Exception
	 */
	public static void init(Map<String, String> lockToServer, ILog log) throws Exception {
		DistributedlockConfig.log = log;
		DistributedLockClient.lockToServer = lockToServer;
		// 映射操作码解析类
		ThreadTcpManager.addThreadMapping(DistributedlockTCode.DISTRIBUTED_LOCK_C1, DistributedLockC1.class, null);
		ThreadTcpManager.addThreadMapping(DistributedlockTCode.DISTRIBUTED_LOCK_S1, DistributedLockS1.class, null);
		ThreadTcpManager.addThreadMapping(DistributedlockTCode.DISTRIBUTED_LOCK_C2, DistributedLockC2.class, null);
	}

	/**
	 * 获取锁id
	 * 
	 * @param key
	 *            锁类型的键值
	 * @param type
	 *            锁类型
	 * @return 0说明获取失败，>0说明获取成功
	 */
	public static int getLock(String key, String type) {
		// 查看有没有映射
		String serverName = lockToServer.get(type);
		if (serverName == null) {
			if (DistributedlockConfig.log != null) {
				DistributedlockConfig.log.warn("类型为：" + type + "未找到对应服务器");
			}
			return 0;
		}
		// 获取服务器session
		MinaClientService minaClientService = (MinaClientService) MsgManager.msgClassInstanceMap.get(MinaClientService.class);
		IoSession ioSession = minaClientService.getServerIoSession(serverName);
		if (ioSession == null) {
			if (DistributedlockConfig.log != null) {
				DistributedlockConfig.log.warn("类型为：" + type + "，未初始化相应服务器或者与此服务器断开");
			}
			return 0;
		}
		// 发申请锁请求并且阻塞
		DistributedLockC1.Builder builder = DistributedLockC1.newBuilder();
		builder.setKey(key);
		builder.setType(type);
		TcpPacket tcpPacket = new TcpPacket(DistributedlockTCode.DISTRIBUTED_LOCK_C1, builder.build());
		TcpPacket returnPT = WaitLockManager.lock(ioSession, tcpPacket);
		if (returnPT == null) {
			return 0;
		}
		// 返回锁id
		DistributedLockS1 distributedLockS1 = (DistributedLockS1) returnPT.getData();
		if (!distributedLockS1.getResult()) {
			return 0;
		}
		return returnPT.lockedId;
	}

	/**
	 * 释放锁
	 * 
	 * @param key
	 *            锁类型的键值
	 * @param type
	 *            锁类型
	 * @param lockId
	 *            锁id
	 */
	public static void unLock(String key, String type, int lockId) {
		// 查看有没有映射
		String serverName = lockToServer.get(type);
		if (serverName == null) {
			if (DistributedlockConfig.log != null) {
				DistributedlockConfig.log.warn("类型为：" + type + "未找到对应服务器");
			}
			return;
		}
		// 获取服务器session
		MinaClientService minaClientService = (MinaClientService) MsgManager.msgClassInstanceMap.get(MinaClientService.class);
		IoSession ioSession = minaClientService.getServerIoSession(serverName);
		if (ioSession == null) {
			if (DistributedlockConfig.log != null) {
				DistributedlockConfig.log.warn("类型为：" + type + "，未初始化相应服务器或者与此服务器断开");
			}
			return;
		}
		// 发释放锁消息
		DistributedLockC2.Builder builder = DistributedLockC2.newBuilder();
		builder.setName("unlock");
		TcpPacket tcpPacket = new TcpPacket(DistributedlockTCode.DISTRIBUTED_LOCK_C2, builder.build());
		tcpPacket.unlockedId = lockId;
		ioSession.write(tcpPacket);
	}
}
