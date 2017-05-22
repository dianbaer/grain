package http;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.servlet.ServletOutputStream;

import http.filter.FileData;
import log.LogManager;

public class ReplyUtil {
	public static boolean Reply(Object replyBody, HSession hSession) {
		if (hSession.headParam.receiveType.equals(AllowParam.RECEIVE_TYPE_JSON)) {
			ServletOutputStream servletOutputStream = null;
			try {
				HttpPacket httpPacket = (HttpPacket) replyBody;
				if (httpPacket == null) {
					return false;
				}
				hSession.response.setContentType(HttpConfig.CONTENT_TYPE_JSON);
				hSession.response.setCharacterEncoding(HttpConfig.ENCODE);
				String sendStr = CodeUtils.encodeJson(httpPacket);
				byte[] sendByte = sendStr.getBytes(HttpConfig.ENCODE);

				hSession.response.setContentLength(sendByte.length);
				servletOutputStream = hSession.response.getOutputStream();
				if (servletOutputStream == null) {
					return false;
				}
				servletOutputStream.write(sendByte, 0, sendByte.length);
				servletOutputStream.flush();
				hSession.putMonitor("回复json完成");
				return true;
			} catch (Exception e) {
				LogManager.httpLog.error("回复json类型数据异常", e);
				return false;
			} finally {
				if (servletOutputStream != null) {
					try {
						servletOutputStream.close();
					} catch (IOException e) {
						LogManager.httpLog.error("关闭回复json流异常", e);
					}
				}
			}

		} else if (hSession.headParam.receiveType.equals(AllowParam.RECEIVE_TYPE_PROTOBUF)) {
			ServletOutputStream servletOutputStream = null;
			try {
				HttpPacket httpPacket = (HttpPacket) replyBody;
				String sendStr = CodeUtils.encodeProtoBuf(httpPacket);
				if (sendStr == null) {
					return false;
				}
				servletOutputStream = hSession.response.getOutputStream();
				if (servletOutputStream == null) {
					return false;
				}
				servletOutputStream.print(sendStr);
				servletOutputStream.flush();
				hSession.putMonitor("回复protobuf完成");
				return true;
			} catch (Exception e) {
				LogManager.httpLog.error("回复protobuf数据异常", e);
				return false;
			} finally {
				if (servletOutputStream != null) {
					try {
						servletOutputStream.close();
					} catch (IOException e) {
						LogManager.httpLog.error("关闭回复protobuf流异常", e);
					}
				}
			}

		} else if (hSession.headParam.receiveType.equals(AllowParam.RECEIVE_TYPE_IMAGE)) {
			InputStream in = null;
			ServletOutputStream out = null;
			try {
				FileData file = (FileData) replyBody;
				if (file == null) {
					LogManager.httpLog.warn("发送文件为空");
					return false;
				}
				if (file.getFile() != null) {
					in = new FileInputStream(file.getFile());
				} else {
					in = file.getStream();
				}
				out = hSession.response.getOutputStream();
				if (out == null) {
					LogManager.httpLog.warn("输出流为空");
					return false;
				}

				hSession.response.setContentType("image/jpeg");

				byte[] buffer = new byte[HttpConfig.DOWNLOAD_BLOCK_SIZE];
				int bytesRead = -1;
				long t1 = System.currentTimeMillis();
				while ((bytesRead = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
					if (HttpConfig.DOWNLOAD_IMAGE_SLEEP_TIME > 0) {
						long t, t2;
						t2 = System.currentTimeMillis();
						t = t2 - t1;
						if (t < HttpConfig.DOWNLOAD_IMAGE_SLEEP_TIME) {
							Thread.sleep(HttpConfig.DOWNLOAD_IMAGE_SLEEP_TIME - t);
						}
						t1 = System.currentTimeMillis();
					}
				}
				out.flush();
				hSession.putMonitor("回复图片流完成");
				return true;
			} catch (Exception e) {
				LogManager.httpLog.error("发送图片异常", e);
				return false;
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						LogManager.httpLog.error("发送图片，关闭输入流异常", e);
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						LogManager.httpLog.error("发送图片，关闭输出流异常", e);
					}
				}
			}
		} else if (hSession.headParam.receiveType.equals(AllowParam.RECEIVE_TYPE_FILE)) {
			InputStream in = null;
			ServletOutputStream out = null;
			try {
				FileData file = (FileData) replyBody;
				if (file == null) {
					LogManager.httpLog.warn("发送文件为空");
					return false;
				}
				if (file.getFile() != null) {
					in = new FileInputStream(file.getFile());
				} else {
					in = file.getStream();
				}
				out = hSession.response.getOutputStream();
				if (out == null) {
					LogManager.httpLog.warn("输出流为空");
					return false;
				}
				String agent = hSession.request.getHeader("user-agent");
				String srcFileName;
				if (agent.contains("Firefox")) {
					srcFileName = new String(file.getFileName().getBytes(HttpConfig.ENCODE), HttpConfig.SEND_CODE);
				} else {
					srcFileName = URLEncoder.encode(file.getFileName(), HttpConfig.ENCODE);
				}
				srcFileName = srcFileName.replaceAll("\\+", "%20").replaceAll("%28", "\\(").replaceAll("%29", "\\)").replaceAll("%3B", ";").replaceAll("%40", "@").replaceAll("%23", "\\#").replaceAll("%26", "\\&");
				hSession.response.setCharacterEncoding(HttpConfig.ENCODE);
				hSession.response.setHeader("Content-disposition", "attachment;filename=\"" + srcFileName + "\"");

				byte[] buffer = new byte[HttpConfig.DOWNLOAD_BLOCK_SIZE];
				int bytesRead = -1;
				long t1 = System.currentTimeMillis();
				while ((bytesRead = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
					if (HttpConfig.DOWNLOAD_FILE_SLEEP_TIME > 0) {
						long t, t2;
						t2 = System.currentTimeMillis();
						t = t2 - t1;
						if (t < HttpConfig.DOWNLOAD_FILE_SLEEP_TIME) {
							Thread.sleep(HttpConfig.DOWNLOAD_FILE_SLEEP_TIME - t);
						}
						t1 = System.currentTimeMillis();
					}
				}
				out.flush();
				hSession.putMonitor("回复文件流完成");
				return true;
			} catch (Exception e) {
				LogManager.httpLog.error("发送文件异常", e);
				return false;
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						LogManager.httpLog.error("发送文件，关闭输入流异常", e);
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						LogManager.httpLog.error("发送文件，关闭输出流异常", e);
					}
				}
			}

		} else if (hSession.headParam.receiveType.equals(AllowParam.RECEIVCE_TYPE_STRING)) {
			ServletOutputStream servletOutputStream = null;
			try {
				String sendStr = (String) replyBody;
				if (sendStr == null) {
					return false;
				}
				hSession.response.setCharacterEncoding(HttpConfig.ENCODE);
				byte[] sendByte = sendStr.getBytes(HttpConfig.ENCODE);
				hSession.response.setContentLength(sendByte.length);
				servletOutputStream = hSession.response.getOutputStream();
				if (servletOutputStream == null) {
					return false;
				}
				servletOutputStream.write(sendByte, 0, sendByte.length);
				servletOutputStream.flush();
				hSession.putMonitor("回复String完成");
				return true;
			} catch (Exception e) {
				LogManager.httpLog.error("回复String类型数据异常", e);
				return false;
			} finally {
				if (servletOutputStream != null) {
					try {
						servletOutputStream.close();
					} catch (IOException e) {
						LogManager.httpLog.error("关闭回复String流异常", e);
					}
				}
			}
		} else if (hSession.headParam.receiveType.equals(AllowParam.RECEIVE_TYPE_NONE)) {
			hSession.putMonitor("无回复内容");
			return true;
		} else if (hSession.headParam.receiveType.equals(AllowParam.RECEIVE_TYPE_OTHER_STREAM)) {
			InputStream in = null;
			ServletOutputStream out = null;
			try {
				FileData file = (FileData) replyBody;
				if (file == null) {
					LogManager.httpLog.warn("发送文件为空");
					return false;
				}
				if (file.getFile() != null) {
					in = new FileInputStream(file.getFile());
				} else {
					in = file.getStream();
				}
				out = hSession.response.getOutputStream();
				if (out == null) {
					LogManager.httpLog.warn("输出流为空");
					return false;
				}

				hSession.response.setContentType(file.getContentType());

				byte[] buffer = new byte[HttpConfig.DOWNLOAD_BLOCK_SIZE];
				int bytesRead = -1;
				long t1 = System.currentTimeMillis();
				while ((bytesRead = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
					if (HttpConfig.DOWNLOAD_IMAGE_SLEEP_TIME > 0) {
						long t, t2;
						t2 = System.currentTimeMillis();
						t = t2 - t1;
						if (t < HttpConfig.DOWNLOAD_IMAGE_SLEEP_TIME) {
							Thread.sleep(HttpConfig.DOWNLOAD_IMAGE_SLEEP_TIME - t);
						}
						t1 = System.currentTimeMillis();
					}
				}
				out.flush();
				hSession.putMonitor("回复二进制流完成");
				return true;
			} catch (Exception e) {
				LogManager.httpLog.error("发送二进制流异常", e);
				return false;
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						LogManager.httpLog.error("发送二进制流，关闭输入流异常", e);
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						LogManager.httpLog.error("发送二进制流，关闭输出流异常", e);
					}
				}
			}
		}

		else {
			return false;
		}

	}
}
