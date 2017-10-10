package org.grain.httpserver;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractMessage.Builder;
import com.google.protobuf.Message;

import net.sf.json.JSONObject;

public class CodeUtils {
	/**
	 * 将HttpPacket转换成字符串
	 * 
	 * @param httpPacket
	 * @return
	 */
	public static String encodeJson(HttpPacket httpPacket) {
		return PacketUtils.protoBufToJson(httpPacket.getData());
	}

	/**
	 * 将字符串转换成HttpPacket
	 * 
	 * @param stringResult
	 *            字符串
	 * @param isServer
	 *            是不是服务器一般传true
	 * @param httpPacket
	 *            消息包
	 * @return
	 */
	public static boolean decodeJson(String stringResult, boolean isServer, HttpPacket httpPacket) {
		try {
			// 转换成json获取hOpCode，如果没有看看头消息有没有
			JSONObject jsObj = JSONObject.fromObject(stringResult);
			String hOpCode;
			if (jsObj.containsKey(AllowParam.HOPCODE)) {
				hOpCode = jsObj.getString(AllowParam.HOPCODE);
			} else if (httpPacket.hSession.headParam.hOpCode != null && !httpPacket.hSession.headParam.hOpCode.equals("")) {
				hOpCode = httpPacket.hSession.headParam.hOpCode;
			} else {
				return false;
			}
			// 是否设定相应解析
			if (!HttpManager.hOpCodeMap.containsKey(hOpCode)) {
				if (HttpConfig.log != null) {
					HttpConfig.log.warn("hOpCode为：" + hOpCode + "无对应解析，请及时解决");
				}
				return false;
			}
			// 解析
			Class<?>[] classNames = HttpManager.hOpCodeMap.get(hOpCode);
			Class<?> className;
			if (isServer) {
				className = classNames[0];
			} else {
				className = classNames[1];
			}
			Method buildM = className.getDeclaredMethod("newBuilder");
			AbstractMessage.Builder<?> builder = (Builder<?>) buildM.invoke(null);
			Message data = PacketUtils.jsonToProtoBuf(stringResult, builder);
			if (data == null) {
				return false;
			}
			// 设置hOpCode和消息体
			httpPacket.sethOpCode(hOpCode);
			httpPacket.setData(data);
			return true;
		} catch (Exception e) {
			if (HttpConfig.log != null) {
				HttpConfig.log.error("json转换成protobuf异常", e);
			}
			return false;
		}
	}

	/**
	 * 将二进制转换成HttpPacket
	 * 
	 * @param byteResult
	 *            二进制
	 * @param isServer
	 *            是不是服务器，一般传true
	 * @param httpPacket
	 *            消息包
	 * @return
	 */
	public static boolean decodeJson(byte[] byteResult, boolean isServer, HttpPacket httpPacket) {
		try {
			String jsonStr = new String(byteResult, HttpConfig.ENCODE);
			return decodeJson(jsonStr, isServer, httpPacket);
		} catch (UnsupportedEncodingException e) {
			if (HttpConfig.log != null) {
				HttpConfig.log.error("json转换成protobuf异常", e);
			}
			return false;
		}

	}
}
