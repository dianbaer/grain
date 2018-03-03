package org.grain.websokcetlib;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractMessage.Builder;
import com.google.protobuf.Message;

import net.sf.json.JSONObject;

public class WSCodeUtil {
	public static String ENCODE = "UTF-8";
	public static String WSOPCODE = "wsOpCode";

	public static String encodeJson(WsPacket wsPacket) {
		return PacketUtils.protoBufToJson(wsPacket.getData());
	}

	public static WsPacket decodeJson(String stringResult) {
		try {

			JSONObject jsObj = JSONObject.fromObject(stringResult);
			String wsOpCode = jsObj.getString(WSOPCODE);
			if (wsOpCode == null) {
				if (WSManager.log != null) {
					WSManager.log.warn("数据为：" + stringResult + "，无wsOpCode");
				}
				return null;
			}
			if (!WSManager.wsOpCodeMap.containsKey(wsOpCode)) {
				if (WSManager.log != null) {
					WSManager.log.warn("wsOpCode为：" + wsOpCode + "无对应解析，请及时解决");
				}
				return null;
			}
			Class<?> className = WSManager.wsOpCodeMap.get(wsOpCode);

			Method buildM = className.getDeclaredMethod("newBuilder");
			AbstractMessage.Builder<?> builder = (Builder<?>) buildM.invoke(null);
			Message data = PacketUtils.jsonToProtoBuf(stringResult, builder);
			WsPacket wsPacket = new WsPacket(wsOpCode, data);
			return wsPacket;
		} catch (Exception e) {
			if (WSManager.log != null) {
				WSManager.log.error("json转换成protobuf异常", e);
			}
			return null;
		}
	}

	public static WsPacket decodeJson(ByteBuffer buffer) {

		try {
			Charset charset = Charset.forName(ENCODE);
			CharsetDecoder decoder = charset.newDecoder();
			CharBuffer charBuffer = decoder.decode(buffer);
			return decodeJson(charBuffer.toString());
		} catch (Exception e) {
			if (WSManager.log != null) {
				WSManager.log.error("json转换成protobuf异常", e);
			}
			return null;
		}

	}
}
