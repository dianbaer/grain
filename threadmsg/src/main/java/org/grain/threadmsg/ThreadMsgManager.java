package org.grain.threadmsg;

import java.util.HashMap;

import org.grain.msg.MsgManager;
import org.grain.msg.MsgPacket;
import org.grain.thread.AsyncThreadManager;
import org.grain.thread.ThreadHandle;

public class ThreadMsgManager {
	/**
	 * 操作类型属于哪个线程哪个优先级，为空算作随机
	 */
	public static HashMap<String, int[]> msgOpcodeType = new HashMap<String, int[]>();

	/**
	 * 添加操作码与线程优先级的映射
	 * 
	 * @param msgOpCode
	 *            操作码
	 * @param threadPriority
	 *            线程优先级 可以为null
	 */
	public static boolean addMapping(String msgOpCode, int[] threadPriority) {
		if (msgOpcodeType.containsKey(msgOpCode)) {
			return false;
		}
		if (threadPriority != null) {
			msgOpcodeType.put(msgOpCode, threadPriority);
		}
		return true;
	}

	/**
	 * 发布消息，推送至异步线程
	 * 
	 * @param msgOpCode
	 *            消息操作码
	 * @param data
	 *            可序列化数据
	 * @param otherData
	 *            其他数据
	 * @return
	 */
	public static boolean dispatchThreadMsg(String msgOpCode, Object data, Object otherData) {
		// 消息未被注册过，不能发送
		if (!MsgManager.msgListenerMap.containsKey(msgOpCode)) {
			if (MsgManager.log != null) {
				MsgManager.log.warn("消息类型：" + msgOpCode + ",不存在，无法注册");
			}
			return false;
		}
		// 创建消息包
		MsgPacket msgPacket = new MsgPacket(msgOpCode, data, otherData, MsgManager.USE_MSG_MONITOR);
		// 获取归属线程和优先级，如果为空则随机一个
		int[] msgTypeArray = msgOpcodeType.get(msgPacket.getMsgOpCode());
		if (msgTypeArray == null || msgTypeArray.length != 2) {
			msgTypeArray = AsyncThreadManager.getRandomThreadPriority();
		}
		msgPacket.putMonitor("分发至线程：" + msgTypeArray[0] + ",优先级：" + msgTypeArray[1]);
		// 初始化回调函数

		ThreadHandle threadHandle = new ThreadHandle(msgPacket, MsgManager.method, null);
		// 加入异步线程
		boolean result = AsyncThreadManager.addHandle(threadHandle, msgTypeArray[0], msgTypeArray[1]);
		return result;
	}

}
