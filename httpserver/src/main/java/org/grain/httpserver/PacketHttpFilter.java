package org.grain.httpserver;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import net.sf.json.JSONObject;

public class PacketHttpFilter implements IHttpFilter {

	@Override
	public boolean httpFilter(HttpPacket httpPacket) {
		// 未经过from解析并且含有post内容，认为完整的消息包就是post内容
		if (!httpPacket.isFromAnalysis && httpPacket.hSession.request.getContentLength() > 0) {
			byte[] dateByte = ReadUtils.read(httpPacket.hSession.request);
			if (dateByte == null) {
				return false;
			}
			boolean result = CodeUtils.decodeJson(dateByte, true, httpPacket);
			if (!result) {
				return false;
			}
			httpPacket.putMonitor("解析json完成");
			return true;
		} else if (httpPacket.hSession.headParam.packet != null && !httpPacket.hSession.headParam.packet.equals("")) {
			// 无论header、param、from只要含有packet关键字就认为是一个完整的消息包
			String stringResult_decode;
			try {
				stringResult_decode = URLDecoder.decode(httpPacket.hSession.headParam.packet, HttpConfig.ENCODE);
			} catch (UnsupportedEncodingException e) {
				if (HttpConfig.log != null) {
					HttpConfig.log.error("解析packet异常", e);
				}
				return false;
			}
			boolean result = CodeUtils.decodeJson(stringResult_decode, true, httpPacket);
			if (!result) {
				return false;
			}
			httpPacket.putMonitor("解析packet完成");
			return true;
		} else {
			// 既没有内容也没有packet的情况，会自动将所有参数拼消息包，头消息的内容map忽略
			Object[] keyArray = httpPacket.hSession.headParam.parameterParam.keySet().toArray();
			JSONObject packet = new JSONObject();
			for (int i = 0; i < keyArray.length; i++) {
				String key = String.valueOf(keyArray[i]);
				String value = httpPacket.hSession.headParam.parameterParam.get(key);
				packet.put(key, value);
			}
			// 消息包可能含有操作码
			if (!packet.containsKey(AllowParam.HOPCODE) && httpPacket.hSession.headParam.hOpCode != null) {
				packet.put(AllowParam.HOPCODE, httpPacket.hSession.headParam.hOpCode);
			}
			boolean result = CodeUtils.decodeJson(packet.toString(), true, httpPacket);
			if (!result) {
				return false;
			}
			httpPacket.putMonitor("拼packet完成");
			return true;
		}
	}

}
