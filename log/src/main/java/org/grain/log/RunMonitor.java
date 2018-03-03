package org.grain.log;

import java.util.ArrayList;
import java.util.List;

public class RunMonitor {
	private List<String> contentList;
	private List<Long> timeList;
	private String type;
	private String opCode;

	/**
	 * 初始化
	 * 
	 * @param type
	 *            类型
	 * @param opCode
	 *            操作，做什么
	 */
	public RunMonitor(String type, String opCode) {
		this.type = type;
		this.opCode = opCode;
		this.contentList = new ArrayList<String>();
		this.timeList = new ArrayList<Long>();
	}

	/**
	 * 记录动作内容，并存储时间
	 * 
	 * @param content
	 *            内容
	 */
	public void putMonitor(String content) {
		long time = System.currentTimeMillis();
		contentList.add(content);
		timeList.add(time);
	}

	/**
	 * 生成日志
	 * 
	 * @param opCode
	 *            操作，做什么
	 * @return
	 */
	public String toString(String opCode) {
		this.opCode = opCode;
		return this.toString();
	}

	/**
	 * 生成日志
	 */
	@Override
	public String toString() {
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

			stringBuilder.append(type + ":");

			stringBuilder.append(opCode).append(",TotalTime:").append(totalTime).append("ms");
			stringBuilder.append("\r\n===========================\r\n");
			return stringBuilder.toString();
		}
		return "contentList < 2";
	}
}
