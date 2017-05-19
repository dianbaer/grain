package websocket;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractMessage.Builder;
import com.google.protobuf.Message;

import log.LogManager;
import net.sf.json.JSONObject;
import util.PacketUtils;
import ws.WsOpCode;
import ws.WsPacket;

public class WSCodeUtil {
	public static String ENCODE = "UTF-8";
	public static String WSOPCODE = "wsOpCode";

	public static String encodeJson(WsPacket wsPacket) {
		return PacketUtils.protoBufToJson(wsPacket.getData());
	}

	public static WsPacket decodeJson(String stringResult) {
		try {

			JSONObject jsObj = JSONObject.fromObject(stringResult);
			int wsOpCode = jsObj.getInt(WSOPCODE);
			if (wsOpCode <= 0) {
				LogManager.websocketLog.warn("数据为：" + stringResult + "，无wsOpCode");
				return null;
			}
			if (!WsOpCode.wsOpCodeMap.containsKey(wsOpCode)) {
				LogManager.websocketLog.warn("wsOpCode为：" + wsOpCode + "无对应解析，请及时解决");
				return null;
			}
			Class<?> className = WsOpCode.wsOpCodeMap.get(wsOpCode);

			Method buildM = className.getDeclaredMethod("newBuilder");
			AbstractMessage.Builder<?> builder = (Builder<?>) buildM.invoke(null);
			Message data = PacketUtils.jsonToProtoBuf(stringResult, builder);
			WsPacket wsPacket = new WsPacket(wsOpCode, data, true);
			return wsPacket;
		} catch (Exception e) {
			LogManager.httpLog.error("json转换成protobuf异常", e);
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
			LogManager.websocketLog.error("json转换成protobuf异常", e);
			return null;
		}

	}
}
