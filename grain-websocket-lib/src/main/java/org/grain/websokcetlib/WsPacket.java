package org.grain.websokcetlib;

import org.grain.log.RunMonitor;

import com.google.protobuf.Message;

public class WsPacket {
	private String wsOpCode;
	private Message data;
	public Object session;
	public RunMonitor runMonitor;

	public WsPacket(String wsOpCode, Message data, boolean useWSMonitor) {
		this.wsOpCode = wsOpCode;
		this.data = data;
		if (useWSMonitor) {
			runMonitor = new RunMonitor("WS", this.wsOpCode);
			putMonitor("解析完WS包");
		}
	}

	public WsPacket(String wsOpCode, Message data) {
		this.wsOpCode = wsOpCode;
		this.data = data;
	}

	public void putMonitor(String content) {
		if (runMonitor != null) {
			runMonitor.putMonitor(content);
		}
	}

	public void openRunMonitor() {
		if (runMonitor == null) {
			runMonitor = new RunMonitor("WS", this.wsOpCode);
		}
	}

	public String getWsOpCode() {
		return wsOpCode;
	}

	public void setWsOpCode(String wsOpCode) {
		this.wsOpCode = wsOpCode;
	}

	public Message getData() {
		return data;
	}

	public void setData(Message data) {
		this.data = data;
	}

	public byte[] getByteData() {
		return this.data.toByteArray();
	}

	public void clear() {
		data = null;
		session = null;
		runMonitor = null;
	}
}
