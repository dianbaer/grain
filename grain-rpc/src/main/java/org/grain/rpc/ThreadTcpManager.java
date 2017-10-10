package org.grain.rpc;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.mina.core.session.IoSession;
import org.grain.tcp.MinaConfig;
import org.grain.tcp.TcpManager;
import org.grain.tcp.TcpPacket;
import org.grain.thread.AsyncThreadManager;
import org.grain.thread.ThreadHandle;

public class ThreadTcpManager {

	public static HashMap<Integer, int[]> tOpCodeType = new HashMap<Integer, int[]>();
	public static Method method;

	public static void init() throws Exception {
		ThreadTcpManager.method = ThreadTcpManager.class.getMethod("handleTcp", new Class[] { TcpPacket.class });
	}

	public static boolean addThreadMapping(int tOpCode, Class<?> clazz, int[] threadPriority) {
		if (TcpManager.tOpCodeMap.containsKey(tOpCode) || tOpCodeType.containsKey(tOpCode)) {
			return false;
		}
		TcpManager.tOpCodeMap.put(tOpCode, clazz);
		if (threadPriority != null) {
			tOpCodeType.put(tOpCode, threadPriority);
		}
		return true;
	}

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
		// 专门的解锁线程
		if (tcpPacket.unlockedId != 0) {
			tcpTypeArray = AsyncThreadManager.getLockThreadPriority(1);
		} else {
			// 一般的tcp消息分发
			tcpTypeArray = tOpCodeType.get(tcpPacket.gettOpCode());
			if (tcpTypeArray == null || tcpTypeArray.length != 2) {
				tcpTypeArray = AsyncThreadManager.getRandomThreadPriority();
			}
		}
		tcpPacket.putMonitor("分发至线程：" + tcpTypeArray[0] + ",优先级：" + tcpTypeArray[1]);

		ThreadHandle threadHandle = new ThreadHandle(tcpPacket, ThreadTcpManager.method, null);

		boolean result = AsyncThreadManager.addHandle(threadHandle, tcpTypeArray[0], tcpTypeArray[1]);
		return result;
	}

	public static boolean handleTcp(TcpPacket tcpPacket) throws Exception {
		tcpPacket.putMonitor("开始处理");
		// 如果有解锁id说明是唤醒锁的返回消息包，并且回调到唤醒线程，此消息包不能进行清理
		if (tcpPacket.unlockedId != 0) {
			// 唤醒
			WaitLockManager.unLock(tcpPacket);
			// 打印日志
			tcpPacket.putMonitor("该消息包，用于回调完成");
			if (MinaConfig.USE_TCP_MONITOR) {
				if (MinaConfig.log != null) {
					MinaConfig.log.info(tcpPacket.runMonitor.toString());
				}
			}
			return true;
		}
		// 没有对应回调函数，返回false
		Method method = TcpManager.tcpListenerMap.get(tcpPacket.gettOpCode());
		if (method == null) {
			if (MinaConfig.log != null) {
				MinaConfig.log.warn("TcpPacket，code为：" + tcpPacket.gettOpCode() + "未找到处理函数");
			}
			tcpPacket.clear();
			return false;
		}
		try {
			// 有锁id说明需要返回值
			if (tcpPacket.lockedId != 0) {
				// 回调
				TcpPacket returnPt = (TcpPacket) method.invoke(TcpManager.tcpInstanceMap.get(method), tcpPacket);
				// 正常不能为空
				if (returnPt != null) {
					// 设置唤醒锁id
					returnPt.unlockedId = tcpPacket.lockedId;
					// 发送
					IoSession session = (IoSession) tcpPacket.session;
					session.write(returnPt);
				} else {
					if (MinaConfig.log != null) {
						MinaConfig.log.warn("要求解锁的类型请求，无法返回值，请及时解决");
					}
				}
			} else {
				// 不需要返回值
				method.invoke(TcpManager.tcpInstanceMap.get(method), tcpPacket);
			}
			tcpPacket.putMonitor("处理完成");
			if (MinaConfig.USE_TCP_MONITOR) {
				if (MinaConfig.log != null) {
					MinaConfig.log.info(tcpPacket.runMonitor.toString());
				}
			}
		} catch (Exception e) {
			if (MinaConfig.log != null) {
				MinaConfig.log.error("TcpPacket,code为：" + tcpPacket.gettOpCode() + "，ITcpListener为：" + method.getClass().getName() + "处理失败", e);
			}
		}
		tcpPacket.clear();
		return true;
	}
}
