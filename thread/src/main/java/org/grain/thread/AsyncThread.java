package org.grain.thread;

import java.util.ArrayList;
import java.util.HashMap;

import org.grain.log.ILog;

public class AsyncThread extends Thread {
	/**
	 * 异步线程轮训间隔，一般100毫秒即可
	 */
	private int asyncThreadCycleInterval;
	/**
	 * 日志接口可以为null
	 */
	private ILog log;
	/**
	 * 此异步线程要处理的所有优先级业务
	 */
	public HashMap<Integer, AsyncHandleData> asyncHandleDataMap = new HashMap<Integer, AsyncHandleData>();

	/**
	 * 
	 * @param asyncThreadCycleInterval
	 *            异步线程轮训间隔，一般100毫秒即可
	 * @param name
	 *            异步线程名字
	 * @param log
	 *            日志接口可以为null
	 */
	public AsyncThread(int asyncThreadCycleInterval, String name, ILog log) {
		this.asyncThreadCycleInterval = asyncThreadCycleInterval;
		this.log = log;
		this.setName(name);
	}

	@Override
	public void run() {
		while (true) {
			long startTime = System.currentTimeMillis();
			Object[] priorityArray = asyncHandleDataMap.keySet().toArray();
			// 轮训所有优先级
			for (int i = 0; i < asyncHandleDataMap.size(); i++) {
				AsyncHandleData asyncHandleData = asyncHandleDataMap.get(priorityArray[i]);
				ArrayList<ICycle> addCycleArray = asyncHandleData.getAddCycleArray();
				ArrayList<ICycle> removeCycleArray = asyncHandleData.getRemoveCycleArray();
				// 处理轮训业务
				for (int j = 0; j < asyncHandleData.cycleArray.size(); j++) {
					ICycle changeCycle = asyncHandleData.cycleArray.get(j);
					try {
						changeCycle.cycle();
					} catch (Exception e) {
						if (log != null) {
							log.error("异步线程异常ICycle cycle:" + changeCycle.getClass().getName(), e);
						}
					}
				}
				// 处理本轮加入轮训队列
				for (int j = 0; j < addCycleArray.size(); j++) {
					ICycle changeCycle = addCycleArray.get(j);
					asyncHandleData.cycleArray.add(changeCycle);
					try {
						// 执行加入时的动作
						changeCycle.onAdd();
					} catch (Exception e) {
						if (log != null) {
							log.error("异步线程异常ICycle onAdd:" + changeCycle.getClass().getName(), e);
						}
					}
				}
				// 处理本轮移除轮训队列
				for (int j = 0; j < removeCycleArray.size(); j++) {
					ICycle changeCycle = removeCycleArray.get(j);
					asyncHandleData.cycleArray.remove(changeCycle);
					try {
						// 执行移除时的动作
						changeCycle.onRemove();
					} catch (Exception e) {
						if (log != null) {
							log.error("异步线程异常ICycle onRemove:" + changeCycle.getClass().getName(), e);
						}
					}
				}
				// 处理本轮消息包
				ArrayList<IHandle> handleArray = asyncHandleData.getHandleArray();
				for (int m = 0; m < handleArray.size(); m++) {
					IHandle handle = handleArray.get(m);
					try {
						// 使用方法 实例 消息包进行回调
						handle.getMethod().invoke(handle.getInstance(), handle.getPacket());
					} catch (Exception e) {
						if (log != null) {
							log.error("异步线程异常handle:", e);
						}
					}
				}
			}
			// 睡眠
			long endTime = System.currentTimeMillis();
			if (endTime - startTime < asyncThreadCycleInterval) {
				try {
					Thread.sleep(asyncThreadCycleInterval - (endTime - startTime));
				} catch (InterruptedException e) {
					if (log != null) {
						log.error("异步线程睡眠异常", e);
					}
				}
			}
		}
	}

}
