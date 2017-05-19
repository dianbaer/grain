package http.filter;

import http.AllowParam;
import http.HSession;

public class CrossDomainHttpFilter implements IHttpFilter {

	@Override
	public boolean httpFilter(HSession hSession) {
		hSession.response.addHeader("Access-Control-Allow-Origin", hSession.request.getHeader("Origin"));

		hSession.response.addHeader("Access-Control-Allow-Headers", AllowParam.HOPCODE + "," + AllowParam.TOKEN + "," + AllowParam.CONTENT_TYPE + "," + AllowParam.SEND_TYPE + "," + AllowParam.RECEIVE_TYPE + "," + AllowParam.FILE_UUID + "," + AllowParam.PACKET);

		hSession.response.addHeader("Access-Control-Allow-Credentials", "true");
		hSession.putMonitor("返回可跨域格式");
		return true;
	}

}
