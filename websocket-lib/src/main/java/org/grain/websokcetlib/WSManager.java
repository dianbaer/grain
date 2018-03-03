package org.grain.websokcetlib;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.grain.log.ILog;

public class WSManager {
	private static Map<String, Method> wsListenerMap = new HashMap<String, Method>();
	private static Map<Method, Object> wsInstanceMap = new HashMap<Method, Object>();
	public static HashMap<String, Class<?>> wsOpCodeMap = new HashMap<String, Class<?>>();
	public static ILog log;
	public static Method method;

	/**
	 * 初始化
	 * 
	 * @param log
	 *            日志可以为null
	 * @throws Exception
	 */
	public static void init(ILog log) throws Exception {
		WSManager.log = log;
		WSManager.method = WSManager.class.getMethod("handleWS", new Class[] { WsPacket.class });
	}

	/**
	 * 添加ws操作码与解析类映射
	 * 
	 * @param wsOpCode
	 *            ws操作码
	 * @param clazz
	 *            protobuf解析类
	 * @return
	 */
	public static boolean addMapping(String wsOpCode, Class<?> clazz) {
		if (wsOpCodeMap.containsKey(wsOpCode)) {
			return false;
		}
		wsOpCodeMap.put(wsOpCode, clazz);
		return true;
	}

	/**
	 * 添加ws监听
	 * 
	 * @param wsListener
	 *            实现IWSListener接口的类
	 * @return
	 * @throws Exception
	 */
	public static boolean addWSListener(IWSListener wsListener) throws Exception {
		Map<String, String> wss = wsListener.getWSs();
		if (wss != null) {
			Object[] wsKeyArray = wss.keySet().toArray();
			for (int i = 0; i < wsKeyArray.length; i++) {
				String ws = String.valueOf(wsKeyArray[i]);
				// 获取回调函数
				Method method = wsListener.getClass().getMethod(wss.get(ws), new Class[] { WsPacket.class });
				// 判断是否含有相应的映射
				if (!wsOpCodeMap.containsKey(ws)) {
					if (log != null) {
						log.warn("消息类型：" + ws + ",不存在，无法注册");
					}
					continue;
				}
				// 一个ws操作码只对应一个回调函数
				if (!wsListenerMap.containsKey(ws)) {
					wsListenerMap.put(ws, method);
				} else {
					if (log != null) {
						log.warn("IWSListener：" + ws + "包含多个，请及时处理");
					}
				}
				// 一个回调函数对应一个实例对象，可以为null说明是静态类
				if (!wsInstanceMap.containsKey(method)) {
					wsInstanceMap.put(method, wsListener);
				} else {
					if (log != null) {
						log.warn(method.getName() + "已经被实例化注册过，请及时处理");
					}
				}
			}
			return true;
		} else {
			if (log != null) {
				log.warn("IWSListener：" + wsListener.getClass().getName() + "关注ws为空");
			}
			return false;
		}
	}

	/**
	 * 消息包分发，当先线程
	 * 
	 * @param wsPacket
	 * @return
	 * @throws Exception
	 */
	public static boolean dispatchWS(WsPacket wsPacket) {
		if (wsPacket == null) {
			if (log != null) {
				log.warn("派发ws包为空");
			}
			return false;
		}
		wsPacket.openRunMonitor();
		return handleWS(wsPacket);
	}

	/**
	 * 消息包对应映射函数回调
	 * 
	 * @param wsPacket
	 * @return
	 * @throws Exception
	 */
	public static boolean handleWS(WsPacket wsPacket) {
		wsPacket.putMonitor("开始处理");
		// 判断有无对应回调函数
		Method method = wsListenerMap.get(wsPacket.getWsOpCode());
		if (method == null) {
			if (log != null) {
				log.warn("WsPacket，code为：" + wsPacket.getWsOpCode() + "未找到处理函数");
			}
			return false;
		}
		try {
			// 回调
			method.invoke(wsInstanceMap.get(method), wsPacket);
			// 打印监控日志
			wsPacket.putMonitor("处理完成");
			if (log != null) {
				log.info(wsPacket.runMonitor.toString());
			}
		} catch (Exception e) {
			if (log != null) {
				log.error("WsPacket,code为：" + wsPacket.getWsOpCode() + "，IWSListener为：" + method.getClass().getName() + "处理失败", e);
			}
		}
		// 清理消息包
		wsPacket.clear();
		return true;
	}
}
