package org.grain.httpserver;

import java.util.List;

import org.grain.log.RunMonitor;

import com.google.protobuf.Message;

public class HttpPacket {
	private String hOpCode;
	private Message data;
	public RunMonitor runMonitor;
	public List<FileData> fileList;
	public HSession hSession;
	public boolean isFromAnalysis = false;

	public HttpPacket() {

	}

	public HttpPacket(String hOpCode, Message data) {
		this.hOpCode = hOpCode;
		this.data = data;
	}

	public void putMonitor(String content) {
		if (runMonitor != null) {
			runMonitor.putMonitor(content);
		}
	}

	public void openRunMonitor() {
		if (runMonitor == null) {
			runMonitor = new RunMonitor("HTTP", this.hOpCode);
		}
	}

	public String gethOpCode() {
		return hOpCode;
	}

	public void sethOpCode(String hOpCode) {
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
		runMonitor = null;
		if (fileList != null) {
			for (int i = 0; i < fileList.size(); i++) {
				FileData fileData = fileList.get(i);
				fileData.getFile().delete();
			}
			fileList = null;
		}
		hSession.clear();
		hSession = null;
	}

	public byte[] getByteData() {
		return this.data.toByteArray();
	}
}
