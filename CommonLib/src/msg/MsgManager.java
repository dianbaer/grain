package msg;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import log.LogManager;
import thread.AsyncThreadManager;

public class MsgManager {
	private static Map<String, ArrayList<Method>> msgListenerMap = new HashMap<String, ArrayList<Method>>();
	private static Map<Method, Object> msgInstanceMap = new HashMap<Method, Object>();
	public static boolean USE_MSG_MONITOR;

	public static void init(boolean useMsgMonitor) {
		USE_MSG_MONITOR = useMsgMonitor;
	}

	public static boolean addMsgListener(IMsgListener msgListener) throws Exception {
		Map<String, String> msgs = msgListener.getMsgs();
		if (msgs != null) {
			Object[] msgKeyArray = msgs.keySet().toArray();
			for (int i = 0; i < msgKeyArray.length; i++) {
				String msg = String.valueOf(msgKeyArray[i]);
				Method method = msgListener.getInstance().getClass().getMethod(msgs.get(msg), new Class[] { MsgPacket.class });
				if (!MsgOpCode.msgOpCodeMap.containsKey(msg)) {
					LogManager.msgLog.warn("消息类型：" + msg + ",不存在，无法注册");
					continue;
				}
				ArrayList<Method> methodList = msgListenerMap.get(msg);
				if (methodList == null) {
					methodList = new ArrayList<Method>();
					msgListenerMap.put(msg, methodList);
				}
				if (!methodList.contains(method)) {
					methodList.add(method);
				} else {
					LogManager.msgLog.warn("IMsgListener：" + method.getClass().getName() + "注册多遍，请及时处理");
				}
				if (!msgInstanceMap.containsKey(method)) {
					msgInstanceMap.put(method, msgListener.getInstance());
				} else {
					LogManager.msgLog.warn(method.getName() + "已经被实例化注册过，请及时处理");
				}
			}

			return true;
		} else {
			LogManager.msgLog.warn("IMsgListener：" + msgListener.getClass().getName() + "监控数据为空");
			return false;
		}
	}

	public static boolean dispatchMsg(MsgPacket msgPacket) {
		if (msgPacket == null) {
			LogManager.msgLog.warn("派发消息包为空");
			return false;
		}
		try {
			sendMsgToThread(msgPacket);
		} catch (Exception e) {
			LogManager.msgLog.error("派发消息失败", e);
			return false;
		}
		return true;
	}

	public static boolean dispatchMsgOnCurrentThread(MsgPacket msgPacket) throws Exception {
		if (msgPacket == null) {
			LogManager.msgLog.warn("派发消息包为空");
			return false;
		}
		msgPacket.putMonitor("在当前线程处理");
		return handleMsg(msgPacket);
	}

	public static boolean handleMsg(MsgPacket msgPacket) throws Exception {
		msgPacket.putMonitor("开始处理");
		ArrayList<Method> methodList = msgListenerMap.get(msgPacket.getMsgOpCode());
		if (methodList == null || methodList.size() == 0) {
			LogManager.msgLog.warn("MsgPacket，code为：" + msgPacket.getMsgOpCode() + "未找到处理函数");
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
				LogManager.msgLog.error("MsgPacket,code为：" + msgPacket.getMsgOpCode() + "，IMsgListener为：" + method.getClass().getName() + "处理失败", e);
			}
		}
		msgPacket.putMonitor("处理全部完成");
		if (MsgManager.USE_MSG_MONITOR) {
			LogManager.msgmonitorLog.info(msgPacket.runMonitor.toString(msgPacket.getMsgOpCode()));
		}
		msgPacket.clear();
		return true;
	}

	public static boolean sendMsgToThread(MsgPacket msgPacket) {
		int[] msgTypeArray = MsgOpCode.msgOpcodeType.get(msgPacket.getMsgOpCode());
		if (msgTypeArray == null || msgTypeArray.length != 2) {
			msgTypeArray = AsyncThreadManager.getRandomThreadPriority();
		}
		msgPacket.putMonitor("分发至线程：" + msgTypeArray[0] + ",优先级：" + msgTypeArray[1]);
		return AsyncThreadManager.addMsgPacket(msgPacket, msgTypeArray[0], msgTypeArray[1]);
	}
}
