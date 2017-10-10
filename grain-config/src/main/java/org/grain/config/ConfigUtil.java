
package org.grain.config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.grain.log.ILog;

import net.sf.json.JSONObject;

public class ConfigUtil {
	/**
	 * 读取模板类型的数据
	 * 
	 * @param filePath
	 *            文件地址
	 * @param log
	 *            日志可以为null
	 * @return
	 */
	public static List<String> readTemplate(String filePath, ILog log) {
		List<String> lineList = null;
		FileInputStream fileInputStream = null;
		BufferedReader bufferedReader = null;
		try {
			fileInputStream = new FileInputStream(filePath);
			lineList = new ArrayList<String>();
			bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream, DefaultConfig.UTF8));
			bufferedReader.readLine();
			bufferedReader.readLine();
			String line;
			while (bufferedReader.ready()) {
				line = bufferedReader.readLine();
				if (line != null) {
					if (line.startsWith("#") || line.startsWith("//"))
						continue;
					lineList.add(line);
				}
			}
			return lineList;
		} catch (Exception e) {
			if (log != null) {
				log.error("读取配置文件出错，文件路径为：" + filePath, e);
			}
			return null;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					if (log != null) {
						log.error("关闭读文件流出错：", e);
					}
				}
			}
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					if (log != null) {
						log.error("关闭读文件流出错：", e);
					}
				}
			}
		}
	}

	/**
	 * 读取json类型数据
	 * 
	 * @param filePath
	 *            文件地址
	 * @param log
	 *            日志可以为null
	 * @return
	 */
	public static JSONObject readJson(String filePath, ILog log) {
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		try {
			inputStream = new FileInputStream(filePath);
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, DefaultConfig.UTF8));
			String lines = "";
			String line = "";
			while (bufferedReader.ready()) {
				line = bufferedReader.readLine();

				lines += line;
			}
			return JSONObject.fromObject(lines);
		} catch (Exception e) {
			if (log != null) {
				log.error("读取配置文件出错，文件路径为：" + filePath, e);
			}
			return null;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					if (log != null) {
						log.error("关闭读文件流出错：", e);
					}
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					if (log != null) {
						log.error("关闭读文件流出错：", e);
					}
				}
			}
		}
	}
}
