package mina;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

import log.LogManager;
import mina.distributedlock.DistributedLockServer;
import mina.waitlock.WaitLockManager;
import tcp.TOpCode;
import tcp.TcpPacket;
import thread.AsyncThreadManager;

public class TcpManager {
	private static Map<Integer, Method> tcpListenerMap = new HashMap<Integer, Method>();
	private static Map<Method, Object> tcpInstanceMap = new HashMap<Method, Object>();

	public static boolean addTcpListener(ITcpListener tcpListener) throws Exception {
		Map<Integer, String> tcps = tcpListener.getTcps();
		if (tcps != null) {
			Object[] tcpKeyArray = tcps.keySet().toArray();
			for (int i = 0; i < tcpKeyArray.length; i++) {
				int tcp = Integer.parseInt(String.valueOf(tcpKeyArray[i]));
				Method method = tcpListener.getInstance().getClass().getMethod(tcps.get(tcp), new Class[] { TcpPacket.class });
				if (!TOpCode.tOpCodeMap.containsKey(tcp)) {
					LogManager.minaLog.warn("消息类型：" + tcp + ",不存在，无法注册");
					continue;
				}
				if (!tcpListenerMap.containsKey(tcp)) {
					tcpListenerMap.put(tcp, method);
				} else {
					LogManager.minaLog.warn("ITcpListener：" + tcp + "包含多个，请及时处理");
				}
				if (!tcpInstanceMap.containsKey(method)) {
					tcpInstanceMap.put(method, tcpListener.getInstance());
				} else {
					LogManager.minaLog.warn(method.getName() + "已经被实例化注册过，请及时处理");
				}
			}
			return true;
		} else {
			LogManager.minaLog.warn("ITcpListener：" + tcpListener.getClass().getName() + "监控数据为空");
			return false;
		}
	}

	public static boolean dispatchTcp(TcpPacket tcpPacket) {
		if (tcpPacket == null) {
			LogManager.minaLog.warn("派发tcp包为空");
			return false;
		}
		try {
			sendTcpToThread(tcpPacket);
		} catch (Exception e) {
			LogManager.minaLog.error("派发tcp失败", e);
			return false;
		}
		return true;
	}

	public static boolean handleTcp(TcpPacket tcpPacket) throws Exception {
		tcpPacket.putMonitor("开始处理");
		if (tcpPacket.unlockedId != 0) {
			WaitLockManager.unLock(tcpPacket);
			tcpPacket.putMonitor("该消息包，用于回调完成");
			if (MinaConfig.USE_TCP_MONITOR) {
				LogManager.tcpmonitorLog.info(tcpPacket.runMonitor.toString(tcpPacket.gettOpCode() + ""));
			}
			return true;
		}
		Method method = tcpListenerMap.get(tcpPacket.gettOpCode());
		if (method == null) {
			LogManager.minaLog.warn("TcpPacket，code为：" + tcpPacket.gettOpCode() + "未找到处理函数");
			return false;
		}
		try {
			if (tcpPacket.lockedId != 0) {
				TcpPacket returnPt = (TcpPacket) method.invoke(tcpInstanceMap.get(method), tcpPacket);
				if (returnPt != null) {
					returnPt.unlockedId = tcpPacket.lockedId;
					IoSession session = (IoSession) tcpPacket.session;
					session.write(returnPt);
				} else {
					LogManager.minaLog.warn("要求解锁的类型请求，无法返回值，请及时解决");
				}
			} else {
				method.invoke(tcpInstanceMap.get(method), tcpPacket);
			}
			tcpPacket.putMonitor("处理完成");
			if (MinaConfig.USE_TCP_MONITOR) {
				LogManager.tcpmonitorLog.info(tcpPacket.runMonitor.toString(tcpPacket.gettOpCode() + ""));
			}
		} catch (Exception e) {
			LogManager.minaLog.error("TcpPacket,code为：" + tcpPacket.gettOpCode() + "，ITcpListener为：" + method.getClass().getName() + "处理失败", e);
		}
		tcpPacket.clear();
		return true;
	}

	public static boolean sendTcpToThread(TcpPacket tcpPacket) {
		int[] tcpTypeArray = TOpCode.tOpCodeType.get(tcpPacket.gettOpCode());
		// 专门的解锁线程
		if (tcpPacket.unlockedId != 0) {
			tcpTypeArray = AsyncThreadManager.getUnLockThreadPriority();
		}
		// 分布式锁获取线程
		int threadId = DistributedLockServer.getTypeKeyThreadBelong(tcpPacket);
		if (threadId != 0) {
			tcpTypeArray = new int[] { threadId, 1 };
		}
		if (tcpTypeArray == null || tcpTypeArray.length != 2) {
			tcpTypeArray = AsyncThreadManager.getRandomThreadPriority();
		}
		// 分布式锁存储线程
		if (threadId == 0) {
			DistributedLockServer.saveTypeKeyThreadBelong(tcpPacket, tcpTypeArray[0]);
		}
		tcpPacket.putMonitor("分发至线程：" + tcpTypeArray[0] + ",优先级：" + tcpTypeArray[1]);
		return AsyncThreadManager.addTcpPacket(tcpPacket, tcpTypeArray[0], tcpTypeArray[1]);

	}
}
