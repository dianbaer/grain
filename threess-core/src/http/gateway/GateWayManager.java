package http.gateway;

import java.util.HashMap;

import http.HSession;
import http.HttpPacket;
import http.HttpUtil;
import http.ReplyUtil;
import log.LogManager;

public class GateWayManager {
	private static HashMap<Integer, String> hOpCodeToUrl;

	public static void init(String cateWayConfigClass) throws Exception {
		IGateWayConfig gateWayConfig = (IGateWayConfig) Class.forName(cateWayConfigClass).newInstance();
		hOpCodeToUrl = gateWayConfig.getUrlMapped();
	}

	public static boolean send(HSession hSession) {
		if (!hOpCodeToUrl.containsKey(hSession.headParam.hOpCode)) {
			return false;
		}
		HttpPacket httpPacket = HttpUtil.send(hSession.httpPacket, hOpCodeToUrl.get(hSession.headParam.hOpCode), hSession.headParam.sendType, hSession.headParam.receiveType, hSession.headParam.token);
		if (httpPacket == null) {
			LogManager.httpLog.warn("网关发送的请求，无返回，请注意");
			return true;
		}
		ReplyUtil.Reply(httpPacket, hSession);
		return true;

	}

	public static boolean contains(HSession hSession) {
		return hOpCodeToUrl.containsKey(hSession.headParam.hOpCode);
	}
}
