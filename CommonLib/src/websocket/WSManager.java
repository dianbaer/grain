package websocket;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import log.LogManager;
import thread.AsyncThreadManager;
import ws.WsOpCode;
import ws.WsPacket;

public class WSManager {
	private static Map<Integer, Method> wsListenerMap = new HashMap<Integer, Method>();
	private static Map<Method, Object> wsInstanceMap = new HashMap<Method, Object>();

	public static boolean addWSListener(IWSListener wsListener) throws Exception {
		Map<Integer, String> wss = wsListener.getWSs();
		if (wss != null) {
			Object[] wsKeyArray = wss.keySet().toArray();
			for (int i = 0; i < wsKeyArray.length; i++) {
				int ws = Integer.parseInt(String.valueOf(wsKeyArray[i]));
				Method method = wsListener.getInstance().getClass().getMethod(wss.get(ws), new Class[] { WsPacket.class });
				if (!WsOpCode.wsOpCodeMap.containsKey(ws)) {
					LogManager.websocketLog.warn("消息类型：" + ws + ",不存在，无法注册");
					continue;
				}
				if (!wsListenerMap.containsKey(ws)) {
					wsListenerMap.put(ws, method);
				} else {
					LogManager.websocketLog.warn("IWSListener：" + ws + "包含多个，请及时处理");
				}
				if (!wsInstanceMap.containsKey(method)) {
					wsInstanceMap.put(method, wsListener.getInstance());
				} else {
					LogManager.websocketLog.warn(method.getName() + "已经被实例化注册过，请及时处理");
				}
			}
			return true;
		} else {
			LogManager.websocketLog.warn("IWSListener：" + wsListener.getClass().getName() + "监控数据为空");
			return false;
		}
	}

	public static boolean dispatchWS(WsPacket wsPacket) {
		if (wsPacket == null) {
			LogManager.websocketLog.warn("派发ws包为空");
			return false;
		}
		try {
			sendWSToThread(wsPacket);
		} catch (Exception e) {
			LogManager.websocketLog.error("派发ws失败", e);
			return false;
		}
		return true;
	}

	public static boolean handleWS(WsPacket wsPacket) throws Exception {
		wsPacket.putMonitor("开始处理");

		Method method = wsListenerMap.get(wsPacket.getWsOpCode());
		if (method == null) {
			LogManager.websocketLog.warn("WsPacket，code为：" + wsPacket.getWsOpCode() + "未找到处理函数");
			return false;
		}
		try {

			method.invoke(wsInstanceMap.get(method), wsPacket);

			wsPacket.putMonitor("处理完成");
			LogManager.wsmonitorLog.info(wsPacket.runMonitor.toString(wsPacket.getWsOpCode() + ""));
		} catch (Exception e) {
			LogManager.websocketLog.error("WsPacket,code为：" + wsPacket.getWsOpCode() + "，IWSListener为：" + method.getClass().getName() + "处理失败", e);
		}
		wsPacket.clear();
		return true;
	}

	public static boolean sendWSToThread(WsPacket wsPacket) {
		int[] wsTypeArray = WsOpCode.wsOpCodeType.get(wsPacket.getWsOpCode());

		if (wsTypeArray == null || wsTypeArray.length != 2) {
			wsTypeArray = AsyncThreadManager.getRandomThreadPriority();
		}

		wsPacket.putMonitor("分发至线程：" + wsTypeArray[0] + ",优先级：" + wsTypeArray[1]);
		return AsyncThreadManager.addWSPacket(wsPacket, wsTypeArray[0], wsTypeArray[1]);

	}
}
