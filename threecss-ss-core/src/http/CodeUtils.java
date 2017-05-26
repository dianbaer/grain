package http;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractMessage.Builder;
import com.google.protobuf.Message;

import log.LogManager;
import net.sf.json.JSONObject;
import util.PacketUtils;

public class CodeUtils {

	public static String encodeProtoBuf(HttpPacket httpPacket) {
		try {

			byte[] hOpCodeByte = IntByteUtils.intToByteArray(httpPacket.gethOpCode());
			byte[] bodyByte = httpPacket.getByteData();

			byte[] sendByte = new byte[hOpCodeByte.length + bodyByte.length];

			System.arraycopy(hOpCodeByte, 0, sendByte, 0, hOpCodeByte.length);

			System.arraycopy(bodyByte, 0, sendByte, hOpCodeByte.length, bodyByte.length);

			String sendBase64 = Base64.encodeBase64String(sendByte);
			return sendBase64;
		} catch (Exception e) {
			LogManager.httpLog.error("序列化异常", e);
			return null;
		}

	}

	public static String encodeJson(HttpPacket httpPacket) {
		return PacketUtils.protoBufToJson(httpPacket.getData());
	}

	public static HttpPacket decodeProtoBuf(byte[] byteResult, boolean isServer) {
		try {
			String receiveStr = new String(byteResult, Charset.forName(HttpConfig.ENCODE));

			byte[] receiveByte = Base64.decodeBase64(receiveStr);
			byte[] hOpCodeByte = new byte[4];
			System.arraycopy(receiveByte, 0, hOpCodeByte, 0, 4);
			int hOpCode = IntByteUtils.byteArrayToInt(hOpCodeByte);

			if (!HOpCode.hOpCodeMap.containsKey(hOpCode)) {
				LogManager.httpLog.warn("hOpCode:" + hOpCode + "不在解析列表里，请及时处理");
				return null;
			}
			Class<?>[] classNames = HOpCode.hOpCodeMap.get(hOpCode);
			Class<?> className;
			if (isServer) {
				className = classNames[0];
			} else {
				className = classNames[1];
			}
			byte[] bodyByte = new byte[receiveByte.length - 4];
			System.arraycopy(receiveByte, 4, bodyByte, 0, receiveByte.length - 4);
			Method m = className.getDeclaredMethod("parseFrom", new Class[] { byte[].class });
			Message data = (Message) m.invoke(null, bodyByte);
			HttpPacket httpPacket = new HttpPacket(hOpCode, data);
			return httpPacket;
		} catch (Exception e) {
			LogManager.httpLog.error("反序列化异常", e);
			return null;
		}
	}

	public static HttpPacket decodeJson(String stringResult, boolean isServer) {
		try {
			String stringResult_decode = URLDecoder.decode(stringResult, HttpConfig.ENCODE);
			JSONObject jsObj = JSONObject.fromObject(stringResult_decode);
			int hOpCode = jsObj.getInt(AllowParam.HOPCODE);
			if (hOpCode <= 0) {
				LogManager.httpLog.warn("数据为：" + stringResult + "，无hOpCode");
				return null;
			}
			if (!HOpCode.hOpCodeMap.containsKey(hOpCode)) {
				LogManager.httpLog.warn("hOpCode为：" + hOpCode + "无对应解析，请及时解决");
				return null;
			}
			Class<?>[] classNames = HOpCode.hOpCodeMap.get(hOpCode);
			Class<?> className;
			if (isServer) {
				className = classNames[0];
			} else {
				className = classNames[1];
			}
			Method buildM = className.getDeclaredMethod("newBuilder");
			AbstractMessage.Builder<?> builder = (Builder<?>) buildM.invoke(null);
			Message data = PacketUtils.jsonToProtoBuf(stringResult_decode, builder);
			HttpPacket httpPacket = new HttpPacket(hOpCode, data);
			return httpPacket;
		} catch (Exception e) {
			LogManager.httpLog.error("json转换成protobuf异常", e);
			return null;
		}
	}

	public static HttpPacket decodeJson(byte[] byteResult, boolean isServer) {

		String jsonStr;
		try {
			jsonStr = new String(byteResult, HttpConfig.ENCODE);
			return decodeJson(jsonStr, isServer);
		} catch (UnsupportedEncodingException e) {
			LogManager.httpLog.error("json转换成protobuf异常", e);
			return null;
		}

	}
}
