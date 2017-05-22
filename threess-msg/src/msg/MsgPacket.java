package msg;

import com.google.protobuf.Message;

import monitor.RunMonitor;

public class MsgPacket {
	private String msgOpCode;
	private Message data;
	private Object otherData;
	public RunMonitor runMonitor;

	public MsgPacket(String msgOpCode, Message data) {
		this.msgOpCode = msgOpCode;
		this.data = data;
	}

	public MsgPacket(String msgOpCode, Message data, boolean useMsgMonitor) {
		this.msgOpCode = msgOpCode;
		this.data = data;
		if (useMsgMonitor) {
			runMonitor = new RunMonitor(RunMonitor.MSG);
			putMonitor("生成Msg消息包");
		}
	}

	public void putMonitor(String content) {
		if (runMonitor != null) {
			runMonitor.putMonitor(content);
		}
	}

	public void clear() {
		data = null;
		otherData = null;
	}

	public String getMsgOpCode() {
		return msgOpCode;
	}

	public void setMsgOpCode(String msgOpCode) {
		this.msgOpCode = msgOpCode;
	}

	public Message getData() {
		return data;
	}

	public void setData(Message data) {
		this.data = data;
	}

	public Object getOtherData() {
		return otherData;
	}

	public void setOtherData(Object otherData) {
		this.otherData = otherData;
	}

}
