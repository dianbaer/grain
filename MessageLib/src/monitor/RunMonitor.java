package monitor;

import java.util.ArrayList;
import java.util.List;

public class RunMonitor {
	public static String HTTP = "HTTP";
	public static String TCP = "TCP";
	public static String MSG = "MSG";
	public static String WS = "WS";
	private List<String> contentList;
	private List<Long> timeList;
	private String type;

	public RunMonitor(String type) {
		this.type = type;
		this.contentList = new ArrayList<String>();
		this.timeList = new ArrayList<Long>();
	}

	public void putMonitor(String content) {
		long time = System.currentTimeMillis();
		contentList.add(content);
		timeList.add(time);
	}

	public String toString(String opCode) {
		if (contentList.size() >= 2) {
			long totalTime = timeList.get(timeList.size() - 1).longValue() - timeList.get(0).longValue();
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("\r\n===========================\r\n");
			stringBuilder.append("RunMonitor:" + type);
			stringBuilder.append("\r\n---------------------------\r\n");
			stringBuilder.append("No.\tContent\tTime");
			for (int i = 0; i < contentList.size(); i++) {
				stringBuilder.append("\r\n").append(i);
				stringBuilder.append("\t").append(contentList.get(i));
				long time = 0;
				if (i != 0) {
					time = timeList.get(i).longValue() - timeList.get(i - 1).longValue();
				}
				stringBuilder.append("\t").append(time).append("ms");
			}
			stringBuilder.append("\r\n---------------------------\r\n");
			if (type.equals(HTTP)) {
				stringBuilder.append("hOpCode:");
			} else if (type.equals(TCP)) {
				stringBuilder.append("TOpCode:");
			} else if (type.equals(MSG)) {
				stringBuilder.append("MsgOpCode:");
			} else if (type.equals(WS)) {
				stringBuilder.append("WsOpCode:");
			} else {
				stringBuilder.append("未知类型opCode:");
			}

			stringBuilder.append(opCode).append(",TotalTime:").append(totalTime).append("ms");
			stringBuilder.append("\r\n===========================\r\n");
			return stringBuilder.toString();
		}
		return "contentList < 2";
	}
}
