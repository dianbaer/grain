package mina.distributedlock;

import java.util.ArrayList;
import java.util.HashMap;

import log.LogManager;
import protobuf.tcp.DistributedLock.DistributedLockC1;
import tcp.TOpCode;
import tcp.TcpPacket;

public class DistributedLockServer {
	private static ArrayList<String> types;
	private static HashMap<String, HashMap<String, Integer>> typeAndKeyToThread = new HashMap<>();

	public static void init(String distributedLockServerClass) throws Exception {
		IDistributedLockServer distributedLockServer = (IDistributedLockServer) Class.forName(distributedLockServerClass).newInstance();
		types = new ArrayList<String>();
		for (int i = 0; i < distributedLockServer.getTypes().length; i++) {
			types.add(distributedLockServer.getTypes()[i]);
		}
	}

	public static boolean saveTypeKeyThreadBelong(TcpPacket tcpPacket, int threadId) {
		// 只有DISTRIBUTED_LOCK_C1才进行设定
		if (tcpPacket.gettOpCode() != TOpCode.DISTRIBUTED_LOCK_C1) {
			return true;
		}
		DistributedLockC1 distributedLockC1 = (DistributedLockC1) tcpPacket.getData();
		String type = distributedLockC1.getType();
		if (!types.contains(type)) {
			LogManager.distributedlockLog.warn("分布式锁类型：" + distributedLockC1.getType() + "不归属于此服务器");
			return false;
		}
		if (!typeAndKeyToThread.containsKey(type)) {
			HashMap<String, Integer> map = new HashMap<>();
			typeAndKeyToThread.put(type, map);
		}
		HashMap<String, Integer> keyToThreadMap = typeAndKeyToThread.get(type);
		keyToThreadMap.put(distributedLockC1.getKey(), threadId);
		return true;
	}

	public static int getTypeKeyThreadBelong(TcpPacket tcpPacket) {
		// 只有DISTRIBUTED_LOCK_C1才进行设定
		if (tcpPacket.gettOpCode() != TOpCode.DISTRIBUTED_LOCK_C1) {
			return 0;
		}
		DistributedLockC1 distributedLockC1 = (DistributedLockC1) tcpPacket.getData();
		String type = distributedLockC1.getType();
		if (!typeAndKeyToThread.containsKey(type)) {
			return 0;
		}
		HashMap<String, Integer> keyToThreadMap = typeAndKeyToThread.get(type);
		if (!keyToThreadMap.containsKey(distributedLockC1.getKey())) {
			return 0;
		}
		return keyToThreadMap.get(distributedLockC1.getKey());
	}
}
