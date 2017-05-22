package http;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.Message;

import http.exception.HttpErrorException;
import http.gateway.GateWayManager;
import log.LogManager;

public class HttpManager {
	private static Map<Integer, Method> httpListenerMap = new HashMap<Integer, Method>();
	private static Map<Method, Object> httpInstanceMap = new HashMap<Method, Object>();

	public static boolean addHttpListener(IHttpListener httpListener) throws Exception {
		Map<Integer, String> https = httpListener.getHttps();
		if (https != null) {
			Object[] httpKeyArray = https.keySet().toArray();
			for (int i = 0; i < httpKeyArray.length; i++) {
				int http = Integer.parseInt(String.valueOf(httpKeyArray[i]));
				Method method = httpListener.getInstance().getClass().getMethod(https.get(http), new Class[] { HSession.class });
				if (!HOpCode.hOpCodeMap.containsKey(http)) {
					LogManager.httpLog.warn("消息类型：" + http + ",不存在，无法注册");
					continue;
				}
				if (!httpListenerMap.containsKey(http)) {
					httpListenerMap.put(http, method);
				} else {
					LogManager.httpLog.warn("IHttpListener：" + http + "包含多个，请及时处理");
				}
				if (!httpInstanceMap.containsKey(method)) {
					httpInstanceMap.put(method, httpListener.getInstance());
				} else {
					LogManager.httpLog.warn(method.getName() + "已经被实例化注册过，请及时处理");
				}
			}
			return true;
		} else {
			LogManager.httpLog.warn("IHttpListener：" + httpListener.getClass().getName() + "监控数据为空");
			return false;
		}
	}

	// hao 修改
	/**
	 * <b>方法说明：</b>
	 * <p>
	 * 自动加载方法，重载了addHttpListener
	 * </p>
	 * 
	 * @param hOpCode
	 * @param method
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	public static boolean addHttpListener(int hOpCode, Method method) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		if (!HOpCode.hOpCodeMap.containsKey(hOpCode)) {
			LogManager.httpLog.warn("消息类型：" + hOpCode + ",不存在，无法注册");
			return false;
		}
		if (!httpListenerMap.containsKey(hOpCode)) {
			httpListenerMap.put(hOpCode, method);
		}
		if (!httpInstanceMap.containsKey(method)) {
			httpInstanceMap.put(method, method.getDeclaringClass().getDeclaredMethod("getInstance", new Class[] {}).invoke(method.getDeclaringClass().newInstance()));
		} else {
			LogManager.httpLog.warn(method.getName() + "已经被实例化注册过，请及时处理");
			return false;
		}
		return true;
	}

	public static boolean handleHttp(HSession hSession) throws Exception {
		Method method = null;
		try {
			if (HttpConfig.IS_GATE_WAY_SERVER && GateWayManager.contains(hSession)) {
				hSession.putMonitor("作为网关服务器转发");
				GateWayManager.send(hSession);
				hSession.putMonitor("作为网关服务器转发完成");
				if (HttpConfig.USE_HTTP_MONITOR) {
					LogManager.httpmonitorLog.info(hSession.runMonitor.toString(hSession.headParam.hOpCode + ""));
				}
				return true;
			}
			method = httpListenerMap.get(hSession.headParam.hOpCode);
			if (method == null) {
				LogManager.httpLog.warn("HttpPacket，code为：" + hSession.headParam.hOpCode + "未找到处理函数");
				return false;
			}

			Object result = method.invoke(httpInstanceMap.get(method), hSession);
			hSession.putMonitor("处理handle完成");
			ReplyUtil.Reply(result, hSession); // 此步是处理service方法返回的结果
			if (HttpConfig.USE_HTTP_MONITOR) {
				LogManager.httpmonitorLog.info(hSession.runMonitor.toString(hSession.headParam.hOpCode + ""));
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			if (e.getCause() instanceof HttpErrorException) {
				HttpErrorException exception = (HttpErrorException) e.getCause();
				Message errorData = exception.getErrorData();
				HttpPacket packet = new HttpPacket(exception.getErrorType(), errorData);
				hSession.putMonitor("服务器返回错误：错误号为：" + errorData);
				ReplyUtil.Reply(packet, hSession);
				if (HttpConfig.USE_HTTP_MONITOR) {
					LogManager.httpmonitorLog.info(hSession.runMonitor.toString(hSession.headParam.hOpCode + ""));
				}
			} else {
				LogManager.httpLog.error("HttpPacket,code为：" + hSession.headParam.hOpCode + "，IHttpListener为：" + (method != null ? method.getClass().getName() : "") + "处理失败", e);
			}
			return false;
		} catch (Exception e) {
			LogManager.httpLog.error("HttpPacket,code为：" + hSession.headParam.hOpCode + "，IHttpListener为：" + (method != null ? method.getClass().getName() : "") + "处理失败", e);
		} finally {
			hSession.clear();
		}
		return true;
	}
}
