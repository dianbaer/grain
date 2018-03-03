package org.grain.httpserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.servlet.ServletOutputStream;

public class ReplyUtil {
	/**
	 * 回复HttpPacket以json的形式
	 * 
	 * @param reply
	 * @param sendHttpPacket
	 * @return
	 */
	public static boolean replyJson(HttpPacket reply, HttpPacket sendHttpPacket) {
		sendHttpPacket.hSession.response.setContentType(HttpConfig.CONTENT_TYPE_JSON);
		String sendStr = CodeUtils.encodeJson(reply);
		return reply(sendStr, sendHttpPacket);
	}

	/**
	 * 回复字符串携带头消息
	 * 
	 * @param reply
	 * @param sendHttpPacket
	 * @return
	 */
	public static boolean replyString(ReplyString reply, HttpPacket sendHttpPacket) {
		sendHttpPacket.hSession.response.setContentType(reply.getContentType());
		return reply(reply.getStr(), sendHttpPacket);
	}

	/**
	 * 回复字符串使用默认头消息text/plain
	 * 
	 * @param reply
	 * @param sendHttpPacket
	 * @return
	 */
	public static boolean replyDefaultString(String reply, HttpPacket sendHttpPacket) {
		sendHttpPacket.hSession.response.setContentType("text/plain");
		return reply(reply, sendHttpPacket);
	}

	/**
	 * 回复文件，以下载的形式
	 * 
	 * @param reply
	 * @param sendHttpPacket
	 * @return
	 */
	public static boolean replyFile(ReplyFile reply, HttpPacket sendHttpPacket) {
		try {
			// 兼容浏览器
			String agent = sendHttpPacket.hSession.request.getHeader("user-agent");
			String srcFileName;
			if (agent.contains("Firefox")) {
				srcFileName = new String(reply.getFileName().getBytes(HttpConfig.ENCODE), "ISO-8859-1");
			} else {
				srcFileName = URLEncoder.encode(reply.getFileName(), HttpConfig.ENCODE);
			}
			srcFileName = srcFileName.replaceAll("\\+", "%20").replaceAll("%28", "\\(").replaceAll("%29", "\\)").replaceAll("%3B", ";").replaceAll("%40", "@").replaceAll("%23", "\\#").replaceAll("%26", "\\&");
			sendHttpPacket.hSession.response.setCharacterEncoding(HttpConfig.ENCODE);
			sendHttpPacket.hSession.response.setHeader("Content-disposition", "attachment;filename=\"" + srcFileName + "\"");
			return replySleep(reply.getFile(), sendHttpPacket, HttpConfig.DOWNLOAD_FILE_SLEEP_TIME);
		} catch (Exception e) {
			if (HttpConfig.log != null) {
				HttpConfig.log.error("replyFile error", e);
			}
			return false;
		}

	}

	public static boolean replyImage(ReplyImage reply, HttpPacket sendHttpPacket) {
		sendHttpPacket.hSession.response.setContentType(reply.getContentType());
		return replySleep(reply.getFile(), sendHttpPacket, HttpConfig.DOWNLOAD_IMAGE_SLEEP_TIME);
	}

	private static boolean replySleep(File file, HttpPacket sendHttpPacket, int sleepTime) {
		InputStream in = null;
		ServletOutputStream out = null;
		try {
			in = new FileInputStream(file);
			out = sendHttpPacket.hSession.response.getOutputStream();
			if (out == null) {
				if (HttpConfig.log != null) {
					HttpConfig.log.warn("输出流为空");
				}
				return false;
			}

			byte[] buffer = new byte[HttpConfig.DOWNLOAD_BLOCK_SIZE];
			int bytesRead = -1;
			long t1 = System.currentTimeMillis();
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				if (sleepTime > 0) {
					long t, t2;
					t2 = System.currentTimeMillis();
					t = t2 - t1;
					if (t < sleepTime) {
						Thread.sleep(sleepTime - t);
					}
					t1 = System.currentTimeMillis();
				}
			}
			out.flush();
			sendHttpPacket.putMonitor("回复流完成");
			return true;
		} catch (Exception e) {
			if (HttpConfig.log != null) {
				HttpConfig.log.error("发送流异常", e);
			}
			return false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					if (HttpConfig.log != null) {
						HttpConfig.log.error("发送流，关闭输入流异常", e);
					}
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					if (HttpConfig.log != null) {
						HttpConfig.log.error("发送流，关闭输出流异常", e);
					}
				}
			}
		}
	}

	public static boolean reply(String reply, HttpPacket sendHttpPacket) {
		ServletOutputStream servletOutputStream = null;
		try {

			sendHttpPacket.hSession.response.setCharacterEncoding(HttpConfig.ENCODE);
			byte[] sendByte = reply.getBytes(HttpConfig.ENCODE);
			sendHttpPacket.hSession.response.setContentLength(sendByte.length);
			servletOutputStream = sendHttpPacket.hSession.response.getOutputStream();
			if (servletOutputStream == null) {
				return false;
			}
			servletOutputStream.write(sendByte, 0, sendByte.length);
			servletOutputStream.flush();
			sendHttpPacket.putMonitor("回复String完成");
			return true;
		} catch (Exception e) {
			if (HttpConfig.log != null) {
				HttpConfig.log.error("回复String类型数据异常", e);
			}
			return false;
		} finally {
			if (servletOutputStream != null) {
				try {
					servletOutputStream.close();
				} catch (IOException e) {
					if (HttpConfig.log != null) {
						HttpConfig.log.error("关闭回复String流异常", e);
					}
				}
			}
		}
	}

}
