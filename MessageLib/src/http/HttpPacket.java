package http;

import com.google.protobuf.Message;

public class HttpPacket {
	private int hOpCode;
	private Message data;

	public HttpPacket(int hOpCode, Message data) {
		this.hOpCode = hOpCode;
		this.data = data;
	}

	public int gethOpCode() {
		return hOpCode;
	}

	public void sethOpCode(int hOpCode) {
		this.hOpCode = hOpCode;
	}

	public Message getData() {
		return data;
	}

	public void setData(Message data) {
		this.data = data;
	}

	public void clear() {
		data = null;
	}

	public byte[] getByteData() {
		return this.data.toByteArray();
	}
}
