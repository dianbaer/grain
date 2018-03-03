package org.grain.tcp;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class TcpManager {
	public static Map<Integer, Method> tcpListenerMap = new HashMap<Integer, Method>();
	public static Map<Method, Object> tcpInstanceMap = new HashMap<Method, Object>();
	public static HashMap<Integer, Class<?>> tOpCodeMap = new HashMap<Integer, Class<?>>();

	/**
	 * 添加tcp操作码与解析类映射
	 * 
	 * @param tOpCode
	 *            tcp操作码
	 * @param clazz
	 *            protobuf解析类
	 * @return
	 */
	public static boolean addMapping(int tOpCode, Class<?> clazz) {
		if (tOpCodeMap.containsKey(tOpCode)) {
			return false;
		}
		tOpCodeMap.put(tOpCode, clazz);
		return true;
	}

	/**
	 * 添加tcp监听
	 * 
	 * @param tcpListener
	 *            监听对象实现ITcpListener接口
	 * @return
	 * @throws Exception
	 */
	public static boolean addTcpListener(ITcpListener tcpListener) throws Exception {
		Map<Integer, String> tcps = tcpListener.getTcps();
		if (tcps != null) {
			Object[] tcpKeyArray = tcps.keySet().toArray();
			for (int i = 0; i < tcpKeyArray.length; i++) {
				int tcp = Integer.parseInt(String.valueOf(tcpKeyArray[i]));
				// 获取tcp回调方法
				Method method = tcpListener.getClass().getMethod(tcps.get(tcp), new Class[] { TcpPacket.class });
				// 如果没做相应的映射，则跳过
				if (!tOpCodeMap.containsKey(tcp)) {
					if (MinaConfig.log != null) {
						MinaConfig.log.warn("消息类型：" + tcp + ",不存在，无法注册");
					}
					continue;
				}
				// 一个tcp操作码只对应一个回调函数
				if (!tcpListenerMap.containsKey(tcp)) {
					tcpListenerMap.put(tcp, method);
				} else {
					if (MinaConfig.log != null) {
						MinaConfig.log.warn("ITcpListener：" + tcp + "包含多个，请及时处理");
					}
				}
				// 一个回调函数对应一个实例对象，可以为null说明是静态类
				if (!tcpInstanceMap.containsKey(method)) {
					tcpInstanceMap.put(method, tcpListener);
				} else {
					if (MinaConfig.log != null) {
						MinaConfig.log.warn(method.getName() + "已经被实例化注册过，请及时处理");
					}
				}
			}
			return true;
		} else {
			if (MinaConfig.log != null) {
				MinaConfig.log.warn("ITcpListener：" + tcpListener.getClass().getName() + "关注tcp为空");
			}
			return false;
		}
	}

	public static boolean dispatchTcp(TcpPacket tcpPacket) throws Exception {
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
		return handleTcp(tcpPacket);
	}

	public static boolean handleTcp(TcpPacket tcpPacket) throws Exception {
		tcpPacket.putMonitor("开始处理");

		// 没有对应回调函数，返回false
		Method method = tcpListenerMap.get(tcpPacket.gettOpCode());
		if (method == null) {
			if (MinaConfig.log != null) {
				MinaConfig.log.warn("TcpPacket，code为：" + tcpPacket.gettOpCode() + "未找到处理函数");
			}
			tcpPacket.clear();
			return false;
		}
		try {
			// 不需要返回值
			method.invoke(tcpInstanceMap.get(method), tcpPacket);
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
