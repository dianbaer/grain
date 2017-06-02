package http.filter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import http.AllowParam;
import http.CodeUtils;
import http.HSession;
import http.HttpConfig;
import http.HttpPacket;
import http.ReadUtils;
import log.LogManager;

public class PacketHttpFilter implements IHttpFilter {

	@Override
	public boolean httpFilter(HSession hSession) {
		if (hSession.headParam.sendType.equals(AllowParam.SEND_TYPE_JSON)) {
			byte[] dateByte = ReadUtils.read(hSession.request);
			if (dateByte == null) {
				return false;
			}
			HttpPacket httpPacket = CodeUtils.decodeJson(dateByte, true);
			if (httpPacket == null) {
				return false;
			}
			hSession.httpPacket = httpPacket;
			hSession.putMonitor("解析json完成");
			return true;
		} else if (hSession.headParam.sendType.equals(AllowParam.SEND_TYPE_PROTOBUF)) {
			byte[] dateByte = ReadUtils.read(hSession.request);
			if (dateByte == null) {
				return false;
			}
			HttpPacket httpPacket = CodeUtils.decodeProtoBuf(dateByte, true);
			if (httpPacket == null) {
				return false;
			}
			hSession.httpPacket = httpPacket;
			hSession.putMonitor("解析protobuf完成");
			return true;
		} else if (hSession.headParam.sendType.equals(AllowParam.SEND_TYPE_PACKET) || hSession.headParam.sendType.equals(AllowParam.SEND_TYPE_FILE_SAVE_SESSION) || hSession.headParam.sendType.equals(AllowParam.SEND_TYPE_FILE_NOT_SAVE)) {
			String stringResult_decode;
			try {
				stringResult_decode = URLDecoder.decode(hSession.headParam.packet, HttpConfig.ENCODE);
			} catch (UnsupportedEncodingException e) {
				LogManager.httpLog.error("解析packet异常", e);
				return false;
			}
			HttpPacket httpPacket = CodeUtils.decodeJson(stringResult_decode, true);
			if (httpPacket == null) {
				return false;
			}
			hSession.httpPacket = httpPacket;
			hSession.putMonitor("解析packet、上传文件完成");
			return true;
		} else if (hSession.headParam.sendType.equals(AllowParam.SEND_TYPE_NONE)) {
			hSession.putMonitor("不携带包体");
			return true;
		} else {
			return false;
		}
	}

}
