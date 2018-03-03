package org.grain.tcp;

import org.grain.log.RunMonitor;

import com.google.protobuf.Message;

public class TcpPacket {
	private int tOpCode;
	public int lockedId = 0;
	public int unlockedId = 0;
	private Message data;
	public Object session;
	public RunMonitor runMonitor;

	/**
	 * 
	 * @param tOpCode
	 *            操作码
	 * @param data
	 *            数据
	 * @param useTcpMonitor
	 *            是否打开监控
	 */
	public TcpPacket(int tOpCode, Message data, boolean useTcpMonitor) {
		this.tOpCode = tOpCode;
		this.data = data;
		if (useTcpMonitor) {
			runMonitor = new RunMonitor("TCP", String.valueOf(this.tOpCode));
			putMonitor("解析完Tcp包");
		}
	}

	/**
	 * 
	 * @param tOpCode
	 *            操作码
	 * @param data
	 *            数据
	 */
	public TcpPacket(int tOpCode, Message data) {
		this.tOpCode = tOpCode;
		this.data = data;

	}

	/**
	 * 增加监控内容
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
	 * 打开监控日志
	 */
	public void openRunMonitor() {
		if (runMonitor == null) {
			runMonitor = new RunMonitor("TCP", String.valueOf(this.tOpCode));
		}
	}

	/**
	 * 清理
	 */
	public void clear() {
		data = null;
		session = null;
		runMonitor = null;
	}

	public int gettOpCode() {
		return tOpCode;
	}

	public void settOpCode(int tOpCode) {
		this.tOpCode = tOpCode;
	}

	public Message getData() {
		return data;
	}

	public void setData(Message data) {
		this.data = data;
	}

	/**
	 * 获取二进制数据
	 * 
	 * @return byte[]
	 */
	public byte[] getByteData() {
		return this.data.toByteArray();
	}

}
