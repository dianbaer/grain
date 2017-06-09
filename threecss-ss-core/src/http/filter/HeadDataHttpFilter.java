package http.filter;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import http.AllowParam;
import http.HOpCode;
import http.HSession;
import http.HeadParam;
import http.HttpConfig;
import log.LogManager;

public class HeadDataHttpFilter implements IHttpFilter {

	@Override
	public boolean httpFilter(HSession hSession) {
		String hOpCodeStr = getHeadData(hSession.request, AllowParam.HOPCODE);
		if (hOpCodeStr == null) {
			LogManager.httpLog.info("hOpCode为：" + hOpCodeStr + "不合法");
			return false;
		}
		int hOpCode = Integer.parseInt(hOpCodeStr);
		if (hOpCode <= 0) {
			LogManager.httpLog.info("hOpCode为：" + hOpCode + "不合法");
			return false;
		}
		if (!HOpCode.hOpCodeMap.containsKey(hOpCode)) {
			LogManager.httpLog.warn("hOpCode为：" + hOpCode + "不在接受请求数据里,请及时处理");
			return false;
		}
		String token = getHeadData(hSession.request, AllowParam.TOKEN);
		String sendType = getHeadData(hSession.request, AllowParam.SEND_TYPE);
		if (sendType == null) {
			sendType = AllowParam.SEND_TYPE_JSON;
		}
		String packet = null;
		if (sendType.equals(AllowParam.SEND_TYPE_FILE_SAVE_SESSION) || sendType.equals(AllowParam.SEND_TYPE_FILE_NOT_SAVE) || sendType.equals(AllowParam.SEND_TYPE_PACKET)) {
			packet = getHeadData(hSession.request, AllowParam.PACKET);
//			try {
//				packet = new String(packet.getBytes(HttpConfig.SEND_CODE), HttpConfig.ENCODE);
//			} catch (UnsupportedEncodingException e) {
//				LogManager.httpLog.error("packet解码错误", e);
//				return false;
//			}
		}
		String fileUuid = null;
		if (sendType.equals(AllowParam.SEND_TYPE_FILE_SAVE_SESSION) || sendType.equals(AllowParam.SEND_TYPE_FILE_NOT_SAVE)) {
			fileUuid = getHeadData(hSession.request, AllowParam.FILE_UUID);
		}
		String receiveType = getHeadData(hSession.request, AllowParam.RECEIVE_TYPE);
		if (receiveType == null) {
			receiveType = AllowParam.RECEIVE_TYPE_JSON;
		}
		HeadParam headParam = new HeadParam();
		headParam.hOpCode = hOpCode;
		headParam.token = token;
		headParam.sendType = sendType;
		headParam.packet = packet;
		headParam.fileUuid = fileUuid;
		headParam.receiveType = receiveType;
		hSession.headParam = headParam;
		hSession.putMonitor("解析完头消息");
		return true;
	}

	public String getHeadData(HttpServletRequest request, String headDataName) {
		String headDataValue = request.getHeader(headDataName);
		if (headDataValue == null || headDataValue.equals("")) {
			headDataValue = request.getParameter(headDataName);
		}
		if (headDataValue == null || headDataValue.equals("")) {
			return null;
		}
		return headDataValue;
	}
}
