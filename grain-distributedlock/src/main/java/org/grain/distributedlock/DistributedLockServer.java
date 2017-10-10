package org.grain.distributedlock;

import java.util.ArrayList;
import java.util.HashMap;

import org.grain.distributedlock.tcp.DistributedLock.DistributedLockC1;
import org.grain.distributedlock.tcp.DistributedLock.DistributedLockC2;
import org.grain.distributedlock.tcp.DistributedLock.DistributedLockS1;
import org.grain.log.ILog;
import org.grain.rpc.ThreadTcpManager;
import org.grain.tcp.TcpPacket;

public class DistributedLockServer {
	private static ArrayList<String> types;
	private static HashMap<String, HashMap<String, Integer>> typeAndKeyToThread = new HashMap<>();

	/**
	 * 初始化锁服务器
	 * 
	 * @param types
	 *            这台锁服务器支持的锁类型
	 * @param log
	 *            日志 可以为null
	 * @throws Exception
	 */
	public static void init(ArrayList<String> types, ILog log) throws Exception {
		DistributedlockConfig.log = log;
		DistributedLockServer.types = types;
		// 映射操作码解析类
		ThreadTcpManager.addThreadMapping(DistributedlockTCode.DISTRIBUTED_LOCK_C1, DistributedLockC1.class, null);
		ThreadTcpManager.addThreadMapping(DistributedlockTCode.DISTRIBUTED_LOCK_S1, DistributedLockS1.class, null);
		ThreadTcpManager.addThreadMapping(DistributedlockTCode.DISTRIBUTED_LOCK_C2, DistributedLockC2.class, null);
	}

	/**
	 * 保存线程归属
	 * 
	 * @param tcpPacket
	 *            消息包
	 * @param threadId
	 *            线程id
	 * @return
	 */
	public static boolean saveTypeKeyThreadBelong(TcpPacket tcpPacket, int threadId) {
		// 只有DISTRIBUTED_LOCK_C1才进行设定
		if (tcpPacket.gettOpCode() != DistributedlockTCode.DISTRIBUTED_LOCK_C1) {
			return true;
		}
		// 查看锁服务器是否支持这个锁类型
		DistributedLockC1 distributedLockC1 = (DistributedLockC1) tcpPacket.getData();
		String type = distributedLockC1.getType();
		if (!types.contains(type)) {
			if (DistributedlockConfig.log != null) {
				DistributedlockConfig.log.warn("分布式锁类型：" + distributedLockC1.getType() + "不归属于此服务器");
			}
			return false;
		}
		// 没有的话初始化
		if (!typeAndKeyToThread.containsKey(type)) {
			HashMap<String, Integer> map = new HashMap<>();
			typeAndKeyToThread.put(type, map);
		}
		// 保存锁类型的锁键值与线程绑定
		HashMap<String, Integer> keyToThreadMap = typeAndKeyToThread.get(type);
		keyToThreadMap.put(distributedLockC1.getKey(), threadId);
		return true;
	}

	/**
	 * 通过消息包获取线程归属
	 * 
	 * @param tcpPacket
	 * @return
	 */
	public static int getTypeKeyThreadBelong(TcpPacket tcpPacket) {
		// 只有DISTRIBUTED_LOCK_C1才进行设定
		if (tcpPacket.gettOpCode() != DistributedlockTCode.DISTRIBUTED_LOCK_C1) {
			return 0;
		}
		// 查看锁服务器是否已经含有这个锁类型
		DistributedLockC1 distributedLockC1 = (DistributedLockC1) tcpPacket.getData();
		String type = distributedLockC1.getType();
		if (!typeAndKeyToThread.containsKey(type)) {
			return 0;
		}
		// 是否已经含有这个锁键值
		HashMap<String, Integer> keyToThreadMap = typeAndKeyToThread.get(type);
		if (!keyToThreadMap.containsKey(distributedLockC1.getKey())) {
			return 0;
		}
		// 返回这个锁类型的键值对应的线程id
		return keyToThreadMap.get(distributedLockC1.getKey());
	}
}
