package org.grain.httpserver;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class URLUtil {

	public static String getRequestUrl(HttpPacket httpPacket, String url, String token) {
		String packet;
		try {
			packet = URLEncoder.encode(CodeUtils.encodeJson(httpPacket), HttpConfig.ENCODE);
		} catch (UnsupportedEncodingException e) {
			HttpConfig.log.error("encode pakcet异常", e);
			return null;
		}
		return url + "?" + AllowParam.HOPCODE + "=" + httpPacket.gethOpCode() + "&" + AllowParam.TOKEN + "=" + token + "&" + AllowParam.PACKET + "=" + packet;
	}
}
