package org.grain.distributedlock;

import org.grain.rpc.ThreadTcpManager;
import org.grain.tcp.MinaConfig;
import org.grain.tcp.TcpPacket;
import org.grain.thread.AsyncThreadManager;
import org.grain.thread.ThreadHandle;

public class DistributedlockTcpManager {

	public static boolean dispatchTcp(TcpPacket tcpPacket) {
		if (tcpPacket == null) {
			if (MinaConfig.log != null) {
				MinaConfig.log.warn("派发tcp包为空");
			}
			return false;
		}
		// 打开监控日志
		if (MinaConfig.USE_TCP_MONITOR) {
			tcpPacket.openRunMonitor();
		}

		int[] tcpTypeArray = null;
		// 说明是获取锁的
		// 分布式锁获取线程
		int threadId = DistributedLockServer.getTypeKeyThreadBelong(tcpPacket);
		if (threadId != 0) {
			tcpTypeArray = new int[] { threadId, 1 };
		}
		// 没有说明还没人要，随机一个
		if (tcpTypeArray == null || tcpTypeArray.length != 2) {
			tcpTypeArray = AsyncThreadManager.getRandomThreadPriority();
		}
		// 没存过，存一次
		if (threadId == 0) {
			DistributedLockServer.saveTypeKeyThreadBelong(tcpPacket, tcpTypeArray[0]);
		}

		tcpPacket.putMonitor("分发至线程：" + tcpTypeArray[0] + ",优先级：" + tcpTypeArray[1]);

		ThreadHandle threadHandle = new ThreadHandle(tcpPacket, ThreadTcpManager.method, null);

		boolean result = AsyncThreadManager.addHandle(threadHandle, tcpTypeArray[0], tcpTypeArray[1]);
		return result;
	}

}
