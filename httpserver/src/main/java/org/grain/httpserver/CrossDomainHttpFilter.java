package org.grain.httpserver;

public class CrossDomainHttpFilter implements IHttpFilter {

	/**
	 * 允许跨域，头消息只允许5个关键字
	 */
	@Override
	public boolean httpFilter(HttpPacket httpPacket) {
		httpPacket.hSession.response.addHeader("Access-Control-Allow-Origin", httpPacket.hSession.request.getHeader("Origin"));
		httpPacket.hSession.response.addHeader("Access-Control-Allow-Headers", AllowParam.HOPCODE + "," + AllowParam.TOKEN + "," + AllowParam.CONTENT_TYPE + "," + AllowParam.FILE_UUID + "," + AllowParam.PACKET);
		httpPacket.hSession.response.addHeader("Access-Control-Allow-Credentials", "true");
		httpPacket.putMonitor("返回可跨域格式");
		return true;
	}

}
