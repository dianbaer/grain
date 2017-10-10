package org.grain.httpserver;

import java.util.Enumeration;

public class HeadDataHttpFilter implements IHttpFilter {

	@Override
	public boolean httpFilter(HttpPacket httpPacket) {
		HeadParam headParam = new HeadParam();
		// 头消息解析
		Enumeration<String> headerNames = httpPacket.hSession.request.getHeaderNames();
		if (headerNames != null) {
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement();
				String headerValue = httpPacket.hSession.request.getHeader(headerName);
				// 找关键字
				if (headerValue != null && !headerValue.equals("")) {
					if (headerName.toLowerCase().equals(AllowParam.HOPCODE.toLowerCase())) {
						headParam.hOpCode = headerValue;
					} else if (headerName.toLowerCase().equals(AllowParam.TOKEN.toLowerCase())) {
						headParam.token = headerValue;
					} else if (headerName.toLowerCase().equals(AllowParam.FILE_UUID.toLowerCase())) {
						headParam.fileUuid = headerValue;
					} else if (headerName.toLowerCase().equals(AllowParam.PACKET.toLowerCase())) {
						headParam.packet = headerValue;
					}
					headParam.headParam.put(headerName, headerValue);
				}
			}
		}
		//param解析
		Enumeration<String> parameterNames = httpPacket.hSession.request.getParameterNames();
		if (parameterNames != null) {
			while (parameterNames.hasMoreElements()) {
				String parameterName = parameterNames.nextElement();
				String parameterValue = httpPacket.hSession.request.getParameter(parameterName);
				//找关键字
				if (parameterValue != null && !parameterValue.equals("")) {
					if (parameterName.equals(AllowParam.HOPCODE)) {
						headParam.hOpCode = parameterValue;
					} else if (parameterName.equals(AllowParam.TOKEN)) {
						headParam.token = parameterValue;
					} else if (parameterName.equals(AllowParam.FILE_UUID)) {
						headParam.fileUuid = parameterValue;
					} else if (parameterName.equals(AllowParam.PACKET)) {
						headParam.packet = parameterValue;
					}
					headParam.parameterParam.put(parameterName, parameterValue);
				}
			}
		}
		httpPacket.hSession.headParam = headParam;
		httpPacket.putMonitor("解析完头消息");
		return true;
	}
}
