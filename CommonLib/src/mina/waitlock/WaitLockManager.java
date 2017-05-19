package mina.waitlock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.session.IoSession;

import log.LogManager;
import tcp.TcpPacket;

public class WaitLockManager {
	public static Map<Integer, WaitLock> waitLockMap = new ConcurrentHashMap<Integer, WaitLock>();
	public static int waitLockTime;

	public static void init(int waitLockTime) {
		WaitLockManager.waitLockTime = waitLockTime;
	}

	private static WaitLock getWaitLock() {
		WaitLock waitLock = new WaitLock();
		waitLockMap.put(waitLock.getInstanceId(), waitLock);
		return waitLock;
	}

	public static TcpPacket lock(IoSession ioSession, TcpPacket tcpPacket) {
		WaitLock waitLock = getWaitLock();
		tcpPacket.lockedId = waitLock.getInstanceId();
		synchronized (waitLock) {
			ioSession.write(tcpPacket);
			try {
				waitLock.wait(waitLockTime);
			} catch (InterruptedException e) {
				LogManager.minaLog.error("获取数据超时", e);
			}
		}
		return waitLock.getTcpPacket();
	}

	public static void unLock(TcpPacket tcpPacket) {
		if (tcpPacket.unlockedId == 0) {
			return;
		}
		WaitLock waitLock = waitLockMap.get(tcpPacket.unlockedId);
		waitLock.setTcpPacket(tcpPacket);
		synchronized (waitLock) {
			waitLock.notify();
		}
		waitLockMap.remove(tcpPacket.unlockedId);
	}
}
