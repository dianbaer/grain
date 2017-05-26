package tcp;

import com.google.protobuf.Message;

import monitor.RunMonitor;

public class TcpPacket {
	private int tOpCode;
	public int lockedId = 0;
	public int unlockedId = 0;
	private Message data;
	public Object session;
	public RunMonitor runMonitor;

	public TcpPacket(int tOpCode, Message data, boolean useTcpMonitor) {
		this.tOpCode = tOpCode;
		this.data = data;
		if (useTcpMonitor) {
			runMonitor = new RunMonitor(RunMonitor.TCP);
			putMonitor("解析完Tcp包");
		}
	}

	public TcpPacket(int tOpCode, Message data) {
		this.tOpCode = tOpCode;
		this.data = data;

	}

	public void putMonitor(String content) {
		if (runMonitor != null) {
			runMonitor.putMonitor(content);
		}
	}

	public void clear() {
		data = null;
		session = null;
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

	public byte[] getByteData() {
		return this.data.toByteArray();
	}

}
