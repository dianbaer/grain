package http;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.protobuf.Message;

import http.exception.HttpErrorException;
import http.filter.CrossDomainHttpFilter;
import http.filter.FileHttpFilter;
import http.filter.HeadDataHttpFilter;
import http.filter.IHttpFilter;
import http.filter.IHttpInitFilter;
import http.filter.PacketHttpFilter;
import log.LogManager;
import net.sf.json.JSONArray;

public class HttpFilterManager {
	private static ArrayList<IHttpFilter> httpFilterList = new ArrayList<IHttpFilter>();

	public static void init(JSONArray jsArray, String sendCode, String encode, String uploadTempFolder, String uploadProgressClass, int downloadBlockSize, int downloadFileSleepTime, int downloadImageSleepTime, int downloadOtherStreamSleepTime, boolean useHttpMonitor, boolean isGateWayServer) throws Exception {
		HttpConfig.SEND_CODE = sendCode;
		HttpConfig.ENCODE = encode;
		HttpConfig.UPLOAD_TEMP_FOLDER = uploadTempFolder;
		HttpConfig.UPLOAD_PROGRESS_CLASS = uploadProgressClass;
		HttpConfig.DOWNLOAD_BLOCK_SIZE = downloadBlockSize;
		HttpConfig.DOWNLOAD_FILE_SLEEP_TIME = downloadFileSleepTime;
		HttpConfig.DOWNLOAD_IMAGE_SLEEP_TIME = downloadImageSleepTime;
		HttpConfig.DOWNLOAD_OTHER_STREAM_SLEEP_TIME = downloadOtherStreamSleepTime;
		HttpConfig.USE_HTTP_MONITOR = useHttpMonitor;
		HttpConfig.IS_GATE_WAY_SERVER = isGateWayServer;
		// 跟业务无关的拦截器，按顺序
		CrossDomainHttpFilter crossDomainHttpFilter = new CrossDomainHttpFilter();
		httpFilterList.add(crossDomainHttpFilter);
		HeadDataHttpFilter headDataHttpFilter = new HeadDataHttpFilter();
		httpFilterList.add(headDataHttpFilter);
		FileHttpFilter fileHttpFilter = new FileHttpFilter();
		httpFilterList.add(fileHttpFilter);
		PacketHttpFilter packetHttpFilter = new PacketHttpFilter();
		httpFilterList.add(packetHttpFilter);
		for (int i = 0; i < jsArray.size(); i++) {
			String className = jsArray.getString(i);
			if (className == null || className.equals("")) {
				LogManager.httpLog.warn("http拦截器为空，请及时处理");
				continue;
			}
			IHttpFilter httpFilter = (IHttpFilter) Class.forName(className).newInstance();
			httpFilterList.add(httpFilter);
			//dingwancheng start
			if (httpFilter instanceof IHttpInitFilter) {
				((IHttpInitFilter)httpFilter).httpInit();
			}
			//dingwancheng end
		}
	}

	public static HSession filter(HttpServletRequest request, HttpServletResponse response) {
		HSession hSession = new HSession(request, response);
		for (int i = 0; i < httpFilterList.size(); i++) {
			IHttpFilter httpFilter = httpFilterList.get(i);
			boolean result = false;
			try {
				result = httpFilter.httpFilter(hSession);
			} catch (HttpErrorException e) {
				// LogManager.httpLog.error("拦截器抛HttpErrorException异常", e);
				// 回复异常信息
				Message errorData = e.getErrorData();
				HttpPacket packet = new HttpPacket(e.getErrorType(), errorData);
				hSession.putMonitor("服务器返回错误：错误号为：" + errorData);
				ReplyUtil.Reply(packet, hSession);
				if (HttpConfig.USE_HTTP_MONITOR) {
					LogManager.httpmonitorLog.info(hSession.runMonitor.toString(hSession.headParam.hOpCode + ""));
				}
				return null;
			}
			if (!result) {
				return null;
			}
		}
		return hSession;
	}
}
