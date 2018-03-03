package org.grain.msg;

import org.grain.log.RunMonitor;

public class MsgPacket {
	private String msgOpCode;
	private Object data;
	private Object otherData;
	public RunMonitor runMonitor;

	/**
	 * 初始化
	 * 
	 * @param msgOpCode
	 *            消息操作码
	 * @param data
	 *            消息数据 可以为null，一般为字符类，可以序列化
	 * @param otherData
	 *            其他数据 可以为null，一般为不能序列化的
	 * @param useMsgMonitor
	 *            是否初始化监控
	 */
	public MsgPacket(String msgOpCode, Object data, Object otherData, boolean useMsgMonitor) {
		this.msgOpCode = msgOpCode;
		this.data = data;
		this.otherData = otherData;
		if (useMsgMonitor) {
			runMonitor = new RunMonitor("Msg", msgOpCode);
			putMonitor("生成Msg消息包");
		}
	}

	/**
	 * 添加监控内容
	 * 
	 * @param content
	 *            内容
	 */
	public void putMonitor(String content) {
		if (runMonitor != null) {
			runMonitor.putMonitor(content);
		}
	}

	/**
	 * 清理
	 */
	public void clear() {
		data = null;
		otherData = null;
		runMonitor = null;
	}

	/**
	 * 获取操作码
	 * 
	 * @return
	 */
	public String getMsgOpCode() {
		return msgOpCode;
	}

	/**
	 * 设置操作码
	 * 
	 * @param msgOpCode
	 */
	public void setMsgOpCode(String msgOpCode) {
		this.msgOpCode = msgOpCode;
	}

	/**
	 * 获取数据
	 * 
	 * @return
	 */
	public Object getData() {
		return data;
	}

	/**
	 * 设置数据
	 * 
	 * @param data
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * 获取其他数据
	 * 
	 * @return
	 */
	public Object getOtherData() {
		return otherData;
	}

	/**
	 * 设置其他数据
	 * 
	 * @param otherData
	 */
	public void setOtherData(Object otherData) {
		this.otherData = otherData;
	}

}
