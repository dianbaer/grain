package org.grain.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.session.IoSession;
import org.grain.tcp.MinaConfig;
import org.grain.tcp.TcpPacket;

public class WaitLockManager {
	public static Map<Integer, WaitLock> waitLockMap = new ConcurrentHashMap<Integer, WaitLock>();
	public static int waitLockTime;

	/**
	 * 初始化 设置rpc过期时间
	 * 
	 * @param waitLockTime
	 *            过期时间一般为120000毫秒 2分钟
	 */
	public static void init(int waitLockTime) {
		WaitLockManager.waitLockTime = waitLockTime;
	}

	/**
	 * 获取锁
	 * 
	 * @return
	 */
	private static WaitLock getWaitLock() {
		WaitLock waitLock = new WaitLock();
		waitLockMap.put(waitLock.getInstanceId(), waitLock);
		return waitLock;
	}

	/**
	 * 获取数据
	 * 
	 * @param ioSession
	 *            链接句柄
	 * @param tcpPacket
	 *            消息包
	 * @return
	 */
	public static TcpPacket lock(IoSession ioSession, TcpPacket tcpPacket) {
		// 获取锁
		WaitLock waitLock = getWaitLock();
		// 设置此进程唯一id
		tcpPacket.lockedId = waitLock.getInstanceId();
		// 同步块
		synchronized (waitLock) {
			// 发送消息
			ioSession.write(tcpPacket);
			try {
				// 等待解锁
				waitLock.wait(waitLockTime);
			} catch (InterruptedException e) {
				if (MinaConfig.log != null) {
					MinaConfig.log.error("获取数据超时", e);
				}
			}
		}
		// 解锁成功返回数据
		return waitLock.getTcpPacket();
	}

	/**
	 * 唤醒同步块
	 * 
	 * @param tcpPacket
	 *            返回的消息包
	 */
	public static void unLock(TcpPacket tcpPacket) {
		// 如果没有此进程唯一的唤醒id则返回
		if (tcpPacket.unlockedId == 0) {
			return;
		}
		// 根据唤醒id获取锁对象
		WaitLock waitLock = waitLockMap.get(tcpPacket.unlockedId);
		// 携带消息包
		waitLock.setTcpPacket(tcpPacket);
		// 唤醒
		synchronized (waitLock) {
			waitLock.notify();
		}
		// 移除数组
		waitLockMap.remove(tcpPacket.unlockedId);
	}
}
