package org.grain.msg;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.grain.log.ILog;

public class MsgManager {
	/**
	 * 消息操作码对应的回调方法数组
	 */
	public static Map<String, ArrayList<Method>> msgListenerMap = new HashMap<String, ArrayList<Method>>();
	/**
	 * 相应的方法对应的实例对象，为空可以理解为静态方法
	 */
	private static Map<Method, Object> msgInstanceMap = new HashMap<Method, Object>();
	public static Map<Class<?>, Object> msgClassInstanceMap = new HashMap<Class<?>, Object>();
	/**
	 * 是否打印监控日志
	 */
	public static boolean USE_MSG_MONITOR;
	public static ILog log;
	public static Method method;

	/**
	 * 初始化
	 * 
	 * @param useMsgMonitor
	 *            是否打印监控日志
	 * @param log
	 *            日志对象，为null不输出
	 * @throws Exception
	 */
	public static void init(boolean useMsgMonitor, ILog log) throws Exception {
		MsgManager.USE_MSG_MONITOR = useMsgMonitor;
		MsgManager.log = log;
		MsgManager.method = MsgManager.class.getMethod("handleMsg", new Class[] { MsgPacket.class });
	}

	/**
	 * 添加消息监听
	 * 
	 * @param msgListener
	 *            实现消息监听接口的类
	 * @return
	 * @throws Exception
	 */
	public static boolean addMsgListener(IMsgListener msgListener) throws Exception {
		Map<String, String> msgs = msgListener.getMsgs();
		if (msgs != null) {
			Object[] msgKeyArray = msgs.keySet().toArray();
			for (int i = 0; i < msgKeyArray.length; i++) {
				String msg = String.valueOf(msgKeyArray[i]);
				// 根据函数名获取回调函数
				Method method = msgListener.getClass().getMethod(msgs.get(msg), new Class[] { MsgPacket.class });
				// 操作码不合法
				if (msg == null || msg.equals("")) {
					if (MsgManager.log != null) {
						MsgManager.log.warn("消息类型为空，无法注册");
					}
					continue;
				}
				ArrayList<Method> methodList = msgListenerMap.get(msg);
				// 如果不存在，初始化并放入map
				if (methodList == null) {
					methodList = new ArrayList<Method>();
					msgListenerMap.put(msg, methodList);
				}
				// 相同的方法无法再次注册
				if (!methodList.contains(method)) {
					methodList.add(method);
				} else {
					if (MsgManager.log != null) {
						MsgManager.log.warn("IMsgListener：" + method.getClass().getName() + "注册多遍，请及时处理");
					}
				}
				// 方法对应的实例对象可以为空，正确使用不会重复
				if (!msgInstanceMap.containsKey(method)) {
					msgInstanceMap.put(method, msgListener);
				} else {
					if (MsgManager.log != null) {
						MsgManager.log.warn(method.getName() + "已经被实例化注册过，请及时处理");
					}
				}
				msgClassInstanceMap.put(msgListener.getClass(), msgListener);
			}
			return true;
		} else {
			if (MsgManager.log != null) {
				MsgManager.log.warn("IMsgListener：" + msgListener.getClass().getName() + "监控数据为空");
			}
			return false;
		}
	}

	/**
	 * 执行消息在当前线程，当不使用异步线程时，这个可以用来解耦
	 * 
	 * @param msgOpCode
	 *            消息操作码
	 * @param data
	 *            可序列化数据
	 * @param otherData
	 *            其他数据
	 * @return
	 * @throws Exception
	 */
	public static boolean dispatchMsg(String msgOpCode, Object data, Object otherData) throws Exception {
		// 消息未被注册过，不能发送
		if (!msgListenerMap.containsKey(msgOpCode)) {
			if (MsgManager.log != null) {
				MsgManager.log.warn("消息类型：" + msgOpCode + ",不存在，无法注册");
			}
			return false;
		}
		// 创建消息包
		MsgPacket msgPacket = new MsgPacket(msgOpCode, data, otherData, USE_MSG_MONITOR);
		msgPacket.putMonitor("在当前线程处理");
		return handleMsg(msgPacket);
	}

	/**
	 * 处理消息
	 * 
	 * @param msgPacket
	 *            消息包
	 * @return
	 * @throws Exception
	 */
	public static boolean handleMsg(MsgPacket msgPacket) throws Exception {
		msgPacket.putMonitor("开始处理");
		ArrayList<Method> methodList = msgListenerMap.get(msgPacket.getMsgOpCode());
		if (methodList == null || methodList.size() == 0) {
			if (MsgManager.log != null) {
				MsgManager.log.warn("MsgPacket，code为：" + msgPacket.getMsgOpCode() + "未找到处理函数");
			}
			return false;
		}
		// 先注册的先接收到，保障顺序
		for (int i = 0; i < methodList.size(); i++) {
			Method method = methodList.get(i);
			try {
				msgPacket.putMonitor("开始处理：" + i + ",方法：" + method.getName());
				method.invoke(msgInstanceMap.get(method), msgPacket);
				msgPacket.putMonitor("处理完成：" + i + ",方法：" + method.getName());
			} catch (Exception e) {
				if (MsgManager.log != null) {
					MsgManager.log.error("MsgPacket,code为：" + msgPacket.getMsgOpCode() + "，IMsgListener为：" + method.getClass().getName() + "处理失败", e);
				}
			}
		}
		msgPacket.putMonitor("处理全部完成");
		if (MsgManager.USE_MSG_MONITOR) {
			if (MsgManager.log != null) {
				MsgManager.log.info(msgPacket.runMonitor.toString());
			}
		}
		msgPacket.clear();
		return true;
	}
}
