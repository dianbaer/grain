package thread;

import java.util.HashMap;

import log.LogManager;
import msg.MsgPacket;
import tcp.TcpPacket;
import ws.WsPacket;

public class AsyncThreadManager {
	private static int asyncThreadNum;
	private static int asyncThreadPriorityNum;
	private static HashMap<Integer, AsyncThread> asyncThreadMap = new HashMap<Integer, AsyncThread>();

	public static void init(int asyncThreadCycleInterval, int asyncThreadNum, int asyncThreadPriorityNum) throws Exception {
		AsyncThreadManager.asyncThreadNum = asyncThreadNum;
		if (AsyncThreadManager.asyncThreadNum < 2) {
			LogManager.threadLog.warn("异步线程数量最少两条");
			throw new Exception("异步线程数量最少两条");
		}
		AsyncThreadManager.asyncThreadPriorityNum = asyncThreadPriorityNum;
		for (int i = 1; i <= AsyncThreadManager.asyncThreadNum; i++) {
			AsyncThread asyncThread = new AsyncThread(asyncThreadCycleInterval, "AsyncThread_" + i);
			asyncThreadMap.put(i, asyncThread);
			for (int j = 1; j <= AsyncThreadManager.asyncThreadPriorityNum; j++) {
				AsyncHandleData asyncHandleData = new AsyncHandleData();
				asyncThread.asyncHandleDataMap.put(j, asyncHandleData);
			}
		}

	}

	public static boolean addTcpPacket(TcpPacket tcpPacket, int threadId, int priority) {
		if (tcpPacket == null) {
			LogManager.threadLog.warn("TcpPacket为空");
			return false;
		}
		AsyncThread asyncThread = asyncThreadMap.get(threadId);
		if (asyncThread == null) {
			LogManager.threadLog.warn("不存在线程id：" + threadId);
			return false;
		}
		AsyncHandleData asyncHandleData = asyncThread.asyncHandleDataMap.get(priority);
		if (asyncHandleData == null) {
			LogManager.threadLog.warn("不存在优先级：" + priority);
			return false;
		}
		try {
			asyncHandleData.tcpPool.put(tcpPacket);
			return true;
		} catch (InterruptedException e) {
			LogManager.threadLog.error("放入TcpPacket至异步线程列队失败", e);
			return false;
		}
	}

	public static boolean addWSPacket(WsPacket wsPacket, int threadId, int priority) {
		if (wsPacket == null) {
			LogManager.threadLog.warn("WsPacket为空");
			return false;
		}
		AsyncThread asyncThread = asyncThreadMap.get(threadId);
		if (asyncThread == null) {
			LogManager.threadLog.warn("不存在线程id：" + threadId);
			return false;
		}
		AsyncHandleData asyncHandleData = asyncThread.asyncHandleDataMap.get(priority);
		if (asyncHandleData == null) {
			LogManager.threadLog.warn("不存在优先级：" + priority);
			return false;
		}
		try {
			asyncHandleData.wsPool.put(wsPacket);
			return true;
		} catch (InterruptedException e) {
			LogManager.threadLog.error("放入WsPacket至异步线程列队失败", e);
			return false;
		}
	}

	public static boolean addMsgPacket(MsgPacket msgPacket, int threadId, int priority) {
		if (msgPacket == null) {
			LogManager.threadLog.warn("MsgPacket为空");
			return false;
		}
		AsyncThread asyncThread = asyncThreadMap.get(threadId);
		if (asyncThread == null) {
			LogManager.threadLog.warn("不存在线程id：" + threadId);
			return false;
		}
		AsyncHandleData asyncHandleData = asyncThread.asyncHandleDataMap.get(priority);
		if (asyncHandleData == null) {
			LogManager.threadLog.warn("不存在优先级：" + priority);
			return false;
		}
		try {
			asyncHandleData.msgPool.put(msgPacket);
			return true;
		} catch (InterruptedException e) {
			LogManager.threadLog.error("放入MsgPacket至异步线程列队失败", e);
			return false;
		}
	}

	public static boolean addInitCycle(ICycle cycle, int threadId, int priority) {
		if (cycle == null) {
			LogManager.threadLog.warn("ICycle为空");
			return false;
		}
		AsyncThread asyncThread = asyncThreadMap.get(threadId);
		if (asyncThread == null) {
			LogManager.threadLog.warn("不存在线程id：" + threadId);
			return false;
		}
		AsyncHandleData asyncHandleData = asyncThread.asyncHandleDataMap.get(priority);
		if (asyncHandleData == null) {
			LogManager.threadLog.warn("不存在优先级：" + priority);
			return false;
		}
		asyncHandleData.initCycleArray.add(cycle);
		return true;

	}

	public static boolean addChangeCycle(IChangeCycle cycle, int threadId, int priority) {
		if (cycle == null) {
			LogManager.threadLog.warn("ICycle为空");
			return false;
		}
		AsyncThread asyncThread = asyncThreadMap.get(threadId);
		if (asyncThread == null) {
			LogManager.threadLog.warn("不存在线程id：" + threadId);
			return false;
		}
		AsyncHandleData asyncHandleData = asyncThread.asyncHandleDataMap.get(priority);
		if (asyncHandleData == null) {
			LogManager.threadLog.warn("不存在优先级：" + priority);
			return false;
		}
		try {
			asyncHandleData.addChangeCycleQueue.put(cycle);
			return true;
		} catch (InterruptedException e) {
			LogManager.threadLog.error("放入ICycle至异步线程列队失败", e);
			return false;
		}

	}

	public static boolean removeChangeCycle(IChangeCycle cycle, int threadId, int priority) {
		if (cycle == null) {
			LogManager.threadLog.warn("ICycle为空");
			return false;
		}
		AsyncThread asyncThread = asyncThreadMap.get(threadId);
		if (asyncThread == null) {
			LogManager.threadLog.warn("不存在线程id：" + threadId);
			return false;
		}
		AsyncHandleData asyncHandleData = asyncThread.asyncHandleDataMap.get(priority);
		if (asyncHandleData == null) {
			LogManager.threadLog.warn("不存在优先级：" + priority);
			return false;
		}
		try {
			asyncHandleData.removeChangeCycleQueue.put(cycle);
			return true;
		} catch (InterruptedException e) {
			LogManager.threadLog.error("移除ICycle至异步线程列队失败", e);
			return false;
		}

	}

	public static int[] getRandomThreadPriority() {
		int thread = (int) (Math.random() * (asyncThreadNum - 1) + 1);
		int priority = (int) (Math.random() * asyncThreadPriorityNum + 1);
		return new int[] { thread, priority };

	}

	public static int[] getRandomThread() {
		int thread = (int) (Math.random() * (asyncThreadNum - 1) + 1);
		return new int[] { thread, 1 };
	}

	public static int[] getUnLockThreadPriority() {
		return new int[] { asyncThreadNum, 1 };
	}

	public static void start() {
		for (int i = 1; i <= AsyncThreadManager.asyncThreadNum; i++) {
			AsyncThread asyncThread = asyncThreadMap.get(i);
			asyncThread.start();
		}
	}
}
