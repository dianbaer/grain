package org.grain.thread;

import java.util.HashMap;

import org.grain.log.ILog;

public class AsyncThreadManager {
	private static int asyncThreadNum;
	private static int asyncThreadPriorityNum;
	private static int lockThreadNum;
	private static ILog log;
	private static HashMap<Integer, AsyncThread> asyncThreadMap = new HashMap<Integer, AsyncThread>();

	/**
	 * 
	 * @param asyncThreadCycleInterval
	 *            异步线程轮训间隔，一般100毫秒即可
	 * @param asyncThreadNum
	 *            异步线程数量 最小是1
	 * @param asyncThreadPriorityNum
	 *            异步线程优先级数量 最小是1
	 * @param lockThreadNum
	 *            锁定线程的数量，不允许随机分配的数量，一般填1即可
	 * @param log
	 *            日志接口可为null
	 * @throws Exception
	 */
	public static void init(int asyncThreadCycleInterval, int asyncThreadNum, int asyncThreadPriorityNum, int lockThreadNum, ILog log) throws Exception {
		AsyncThreadManager.asyncThreadNum = asyncThreadNum;
		AsyncThreadManager.asyncThreadPriorityNum = asyncThreadPriorityNum;
		AsyncThreadManager.lockThreadNum = lockThreadNum;
		AsyncThreadManager.log = log;
		if (asyncThreadNum <= lockThreadNum) {
			if (AsyncThreadManager.log != null) {
				AsyncThreadManager.log.warn("总线程数量必须大于锁定线程数量");
			}
			throw new Exception("总线程数量必须大于锁定线程数量");
		}

		if (AsyncThreadManager.asyncThreadNum < 1 || AsyncThreadManager.asyncThreadPriorityNum < 1) {
			if (AsyncThreadManager.log != null) {
				AsyncThreadManager.log.warn("异步线程数量与优先级至少有一个");
			}
			throw new Exception("异步线程数量与优先级至少有一个");
		}

		for (int i = 1; i <= AsyncThreadManager.asyncThreadNum; i++) {
			AsyncThread asyncThread = new AsyncThread(asyncThreadCycleInterval, "AsyncThread_" + i, log);
			asyncThreadMap.put(i, asyncThread);
			for (int j = 1; j <= AsyncThreadManager.asyncThreadPriorityNum; j++) {
				AsyncHandleData asyncHandleData = new AsyncHandleData();
				asyncThread.asyncHandleDataMap.put(j, asyncHandleData);
			}
		}

	}

	/**
	 * 
	 * @param handle
	 *            需要异步线程处理的事情
	 * @param threadId
	 *            线程
	 * @param priority
	 *            优先级
	 * @return true加入队列 false未加入队列
	 */
	public static boolean addHandle(IHandle handle, int threadId, int priority) {
		if (handle == null) {
			if (AsyncThreadManager.log != null) {
				AsyncThreadManager.log.warn("handle为空");
			}
			return false;
		}
		AsyncThread asyncThread = asyncThreadMap.get(threadId);
		if (asyncThread == null) {
			if (AsyncThreadManager.log != null) {
				AsyncThreadManager.log.warn("不存在线程id：" + threadId);
			}
			return false;
		}
		AsyncHandleData asyncHandleData = asyncThread.asyncHandleDataMap.get(priority);
		if (asyncHandleData == null) {
			if (AsyncThreadManager.log != null) {
				AsyncThreadManager.log.warn("不存在优先级：" + priority);
			}
			return false;
		}
		try {
			asyncHandleData.waitHandleQueue.put(handle);
			return true;
		} catch (InterruptedException e) {
			if (AsyncThreadManager.log != null) {
				AsyncThreadManager.log.error("放入handle至异步线程列队失败", e);
			}
			return false;
		}
	}

	/**
	 * 
	 * @param cycle
	 *            将轮训加入异步线程
	 * @param threadId
	 *            线程
	 * @param priority
	 *            优先级
	 * @return true进入加入队列 false未进入加入对接
	 */
	public static boolean addCycle(ICycle cycle, int threadId, int priority) {
		if (cycle == null) {
			if (AsyncThreadManager.log != null) {
				AsyncThreadManager.log.warn("ICycle为空");
			}
			return false;
		}
		AsyncThread asyncThread = asyncThreadMap.get(threadId);
		if (asyncThread == null) {
			if (AsyncThreadManager.log != null) {
				AsyncThreadManager.log.warn("不存在线程id：" + threadId);
			}
			return false;
		}
		AsyncHandleData asyncHandleData = asyncThread.asyncHandleDataMap.get(priority);
		if (asyncHandleData == null) {
			if (AsyncThreadManager.log != null) {
				AsyncThreadManager.log.warn("不存在优先级：" + priority);
			}
			return false;
		}
		try {
			asyncHandleData.waitAddCycleQueue.put(cycle);
			return true;
		} catch (InterruptedException e) {
			if (AsyncThreadManager.log != null) {
				AsyncThreadManager.log.error("放入ICycle至异步线程列队失败", e);
			}
			return false;
		}

	}

	/**
	 * 
	 * @param cycle
	 *            将轮训移除异步线程
	 * @param threadId
	 *            线程
	 * @param priority
	 *            优先级
	 * @return true进入移除队列 false未进入移除对接
	 */
	public static boolean removeCycle(ICycle cycle, int threadId, int priority) {
		if (cycle == null) {
			if (AsyncThreadManager.log != null) {
				AsyncThreadManager.log.warn("ICycle为空");
			}
			return false;
		}
		AsyncThread asyncThread = asyncThreadMap.get(threadId);
		if (asyncThread == null) {
			if (AsyncThreadManager.log != null) {
				AsyncThreadManager.log.warn("不存在线程id：" + threadId);
			}
			return false;
		}
		AsyncHandleData asyncHandleData = asyncThread.asyncHandleDataMap.get(priority);
		if (asyncHandleData == null) {
			if (AsyncThreadManager.log != null) {
				AsyncThreadManager.log.warn("不存在优先级：" + priority);
			}
			return false;
		}
		try {
			asyncHandleData.waitRemoveCycleQueue.put(cycle);
			return true;
		} catch (InterruptedException e) {
			if (AsyncThreadManager.log != null) {
				AsyncThreadManager.log.error("移除ICycle至异步线程列队失败", e);
			}
			return false;
		}

	}

	/**
	 * 获取随机线程和优先级
	 * 
	 * @return int[thread,priority]
	 */
	public static int[] getRandomThreadPriority() {
		int thread = (int) (Math.random() * (asyncThreadNum - lockThreadNum) + 1);
		int priority = (int) (Math.random() * asyncThreadPriorityNum + 1);
		return new int[] { thread, priority };

	}

	/**
	 * 获取随机线程优先级1
	 * 
	 * @return int[thread,1]
	 */
	public static int[] getRandomThread() {
		int thread = (int) (Math.random() * (asyncThreadNum - lockThreadNum) + 1);
		return new int[] { thread, 1 };
	}

	/**
	 * 获取锁定线程第几条
	 * 
	 * @param lockNum
	 *            锁定线程的第几条
	 * @return
	 */
	public static int[] getLockThreadPriority(int lockNum) {
		return new int[] { asyncThreadNum - lockThreadNum + lockNum, 1 };
	}

	/**
	 * 启动
	 */
	public static void start() {
		for (int i = 1; i <= AsyncThreadManager.asyncThreadNum; i++) {
			AsyncThread asyncThread = asyncThreadMap.get(i);
			asyncThread.start();
		}
	}
}
