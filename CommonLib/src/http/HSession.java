package http;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import http.filter.FileData;
import monitor.RunMonitor;

public class HSession {
	public HttpServletRequest request;
	public HttpServletResponse response;
	public HttpSession httpSession;
	public HttpPacket httpPacket;
	public HeadParam headParam;
	public List<FileData> fileList;
	public RunMonitor runMonitor;
	public Object otherData;

	public HSession(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.httpSession = request.getSession();
		if (HttpConfig.USE_HTTP_MONITOR) {
			runMonitor = new RunMonitor(RunMonitor.HTTP);
			putMonitor("生成HSession");
		}
	}

	public void putMonitor(String content) {
		if (runMonitor != null) {
			runMonitor.putMonitor(content);
		}
	}

	public void clear() {
		this.request = null;
		this.response = null;
		this.httpSession = null;
		this.httpPacket = null;
		this.headParam = null;
		if (fileList != null) {
			for (int i = 0; i < fileList.size(); i++) {
				FileData fileData = fileList.get(i);
				fileData.getFile().delete();
			}
		}
	}
}
