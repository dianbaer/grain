package ws;

import com.google.protobuf.Message;

import monitor.RunMonitor;

public class WsPacket {
	private int wsOpCode;
	private Message data;
	public Object session;
	public RunMonitor runMonitor;

	public WsPacket(int wsOpCode, Message data, boolean useWSMonitor) {
		this.wsOpCode = wsOpCode;
		this.data = data;
		if (useWSMonitor) {
			runMonitor = new RunMonitor(RunMonitor.WS);
			putMonitor("解析完WS包");
		}
	}

	public WsPacket(int wsOpCode, Message data) {
		this.wsOpCode = wsOpCode;
		this.data = data;
	}

	public void putMonitor(String content) {
		if (runMonitor != null) {
			runMonitor.putMonitor(content);
		}
	}

	public int getWsOpCode() {
		return wsOpCode;
	}

	public void setWsOpCode(int wsOpCode) {
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
	}
}
