package org.grain.httpserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

import javax.servlet.http.HttpServletRequest;

public class ReadUtils {

	public static byte[] read(HttpServletRequest request) {
		InputStream inputStream = null;
		try {
			if (request.getContentLength() <= 0) {
				if (HttpConfig.log != null) {
					HttpConfig.log.warn("http请求内容为空");
				}
				return null;
			}
			inputStream = request.getInputStream();
			byte[] buffer = new byte[request.getContentLength()];
			int size = inputStream.read(buffer);
			while (buffer.length != request.getContentLength() && size > -1) {
				size = inputStream.read(buffer, buffer.length, request.getContentLength() - buffer.length);
			}
			return buffer;
		} catch (SocketTimeoutException e) {
			if (HttpConfig.log != null) {
				HttpConfig.log.error("读取http输入流异常", e);
			}
			return null;
		} catch (Exception e) {
			if (HttpConfig.log != null) {
				HttpConfig.log.error("读取http输入流异常", e);
			}
			return null;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					if (HttpConfig.log != null) {
						HttpConfig.log.error("关闭http输入流异常", e);
					}
				}
			}
		}
	}
}
