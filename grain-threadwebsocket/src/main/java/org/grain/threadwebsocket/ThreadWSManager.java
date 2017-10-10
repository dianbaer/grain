package org.grain.threadwebsocket;

import java.util.HashMap;

import org.grain.thread.AsyncThreadManager;
import org.grain.thread.ThreadHandle;
import org.grain.websokcetlib.WSManager;
import org.grain.websokcetlib.WsPacket;

public class ThreadWSManager {
	public static HashMap<String, int[]> wsOpCodeType = new HashMap<String, int[]>();

	/**
	 * 注册操作码与映射类和线程优先级的映射
	 * 
	 * @param wsOpCode
	 *            操作码
	 * @param clazz
	 *            映射类
	 * @param threadPriority
	 *            线程优先级，传空说明随机线程
	 * @return
	 */
	public static boolean addThreadMapping(String wsOpCode, Class<?> clazz, int[] threadPriority) {
		if (WSManager.wsOpCodeMap.containsKey(wsOpCode) || wsOpCodeType.containsKey(wsOpCode)) {
			return false;
		}
		WSManager.wsOpCodeMap.put(wsOpCode, clazz);
		if (threadPriority != null) {
			wsOpCodeType.put(wsOpCode, threadPriority);
		}
		return true;
	}

	/**
	 * 将消息包分发到系统多线程模型进行处理
	 * 
	 * @param wsPacket
	 * @return
	 */
	public static boolean dispatchWS(WsPacket wsPacket) {
		if (wsPacket == null) {
			if (WSManager.log != null) {
				WSManager.log.warn("派发ws包为空");
			}
			return false;
		}
		wsPacket.openRunMonitor();
		int[] wsTypeArray = wsOpCodeType.get(wsPacket.getWsOpCode());

		if (wsTypeArray == null || wsTypeArray.length != 2) {
			wsTypeArray = AsyncThreadManager.getRandomThreadPriority();
		}

		wsPacket.putMonitor("分发至线程：" + wsTypeArray[0] + ",优先级：" + wsTypeArray[1]);

		ThreadHandle threadHandle = new ThreadHandle(wsPacket, WSManager.method, null);

		boolean result = AsyncThreadManager.addHandle(threadHandle, wsTypeArray[0], wsTypeArray[1]);
		return result;
	}
}
