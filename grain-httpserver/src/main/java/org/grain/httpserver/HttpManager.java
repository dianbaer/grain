package org.grain.httpserver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpManager {
	private static Map<String, Method> httpListenerMap = new HashMap<String, Method>();
	private static Map<Method, Object> httpInstanceMap = new HashMap<Method, Object>();
	public static HashMap<String, Class<?>[]> hOpCodeMap = new HashMap<String, Class<?>[]>();
	private static ArrayList<IHttpFilter> httpFilterList = new ArrayList<IHttpFilter>();

	private static Map<Class<?>, Method> replayMap = new HashMap<Class<?>, Method>();

	/**
	 * 初始化
	 * 
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	public static void init() throws NoSuchMethodException, SecurityException {
		// 需按顺序 （支持跨域>头消息过滤>文件from表单过滤>消息包过滤）
		IHttpFilter filter = new CrossDomainHttpFilter();
		httpFilterList.add(filter);
		filter = new HeadDataHttpFilter();
		httpFilterList.add(filter);
		filter = new FileHttpFilter();
		httpFilterList.add(filter);
		filter = new PacketHttpFilter();
		httpFilterList.add(filter);
		Method method = ReplyUtil.class.getMethod("replyJson", new Class[] { HttpPacket.class, HttpPacket.class });
		replayMap.put(HttpPacket.class, method);
		method = ReplyUtil.class.getMethod("replyString", new Class[] { ReplyString.class, HttpPacket.class });
		replayMap.put(ReplyString.class, method);
		method = ReplyUtil.class.getMethod("replyDefaultString", new Class[] { String.class, HttpPacket.class });
		replayMap.put(String.class, method);
		method = ReplyUtil.class.getMethod("replyFile", new Class[] { ReplyFile.class, HttpPacket.class });
		replayMap.put(ReplyFile.class, method);
		method = ReplyUtil.class.getMethod("replyImage", new Class[] { ReplyImage.class, HttpPacket.class });
		replayMap.put(ReplyImage.class, method);
	}

	/**
	 * 添加过滤器
	 * 
	 * @param httpFilter
	 * @return
	 */
	public static boolean addFilter(IHttpFilter httpFilter) {
		httpFilterList.add(httpFilter);
		return true;
	}

	/**
	 * 添加回复格式 默认支持5种基本完全覆盖，一般不用添加只是预留接口
	 * 
	 * @param clazz
	 *            回复对象的类
	 * @param method
	 *            回复方法
	 * @return
	 */
	public static boolean addReply(Class<?> clazz, Method method) {
		replayMap.put(clazz, method);
		return true;
	}

	/**
	 * 过滤
	 * 
	 * @param request
	 * @param response
	 * @return HttpPacket空说明过滤失败
	 */
	public static HttpPacket filter(HttpServletRequest request, HttpServletResponse response) {
		HttpPacket httpPacket = new HttpPacket();
		HSession hSession = new HSession();
		hSession.request = request;
		hSession.response = response;
		httpPacket.hSession = hSession;
		httpPacket.openRunMonitor();
		for (int i = 0; i < httpFilterList.size(); i++) {
			IHttpFilter httpFilter = httpFilterList.get(i);
			boolean result = false;
			try {
				result = httpFilter.httpFilter(httpPacket);
			} catch (HttpException e) {
				// json格式的错误提醒
				HttpPacket errorPacket = new HttpPacket(e.getErrorType(), e.getErrorData());
				httpPacket.putMonitor("服务器返回错误：错误号为：" + e.getErrorData());
				ReplyUtil.replyJson(errorPacket, httpPacket);
			}
			if (!result) {
				if (HttpConfig.log != null) {
					HttpConfig.log.info(httpPacket.runMonitor.toString(httpPacket.gethOpCode()));
				}
				httpPacket.clear();
				return null;
			}
		}
		return httpPacket;
	}

	/**
	 * 添加操作码与解析类映射
	 * 
	 * @param hOpCode
	 *            操作码
	 * @param clazz
	 *            客户端发送的消息包
	 * @param clazz1
	 *            服务器回复的消息包
	 * @return
	 */
	public static boolean addMapping(String hOpCode, Class<?> clazz, Class<?> clazz1) {
		if (hOpCodeMap.containsKey(hOpCode)) {
			return false;
		}
		Class<?>[] sendAndReturn = new Class[2];
		sendAndReturn[0] = clazz;
		sendAndReturn[1] = clazz1;
		hOpCodeMap.put(hOpCode, sendAndReturn);
		return true;
	}

	/**
	 * 注册监听 实现IHttpListener接口，对应的http请求会进行回调
	 * 
	 * @param httpListener
	 * @return
	 * @throws Exception
	 */
	public static boolean addHttpListener(IHttpListener httpListener) throws Exception {
		Map<String, String> https = httpListener.getHttps();
		if (https != null) {
			Object[] httpKeyArray = https.keySet().toArray();
			for (int i = 0; i < httpKeyArray.length; i++) {
				String http = String.valueOf(httpKeyArray[i]);
				Method method = httpListener.getClass().getMethod(https.get(http), new Class[] { HttpPacket.class });
				if (!hOpCodeMap.containsKey(http)) {
					if (HttpConfig.log != null) {
						HttpConfig.log.warn("消息类型：" + http + ",不存在，无法注册");
					}
					continue;
				}
				if (!httpListenerMap.containsKey(http)) {
					httpListenerMap.put(http, method);
				} else {
					if (HttpConfig.log != null) {
						HttpConfig.log.warn("IHttpListener：" + http + "包含多个，请及时处理");
					}
				}
				if (!httpInstanceMap.containsKey(method)) {
					httpInstanceMap.put(method, httpListener);
				} else {
					if (HttpConfig.log != null) {
						HttpConfig.log.warn(method.getName() + "已经被实例化注册过，请及时处理");
					}
				}
			}
			return true;
		} else {
			if (HttpConfig.log != null) {
				HttpConfig.log.warn("IHttpListener：" + httpListener.getClass().getName() + "HTTP注册数据为空");
			}
			return false;
		}
	}

	/**
	 * 处理http请求分发
	 * 
	 * @param httpPacket
	 * @return
	 * @throws Exception
	 */
	public static boolean handleHttp(HttpPacket httpPacket) throws Exception {
		Method method = null;
		try {
			method = httpListenerMap.get(httpPacket.gethOpCode());
			if (method == null) {
				if (HttpConfig.log != null) {
					HttpConfig.log.warn("HttpPacket，code为：" + httpPacket.gethOpCode() + "未找到处理函数");
				}
				return false;
			}
			Object result = method.invoke(httpInstanceMap.get(method), httpPacket);
			httpPacket.putMonitor("处理handle完成");
			Method replyMethod = replayMap.get(result.getClass());
			boolean replyResult = (boolean) replyMethod.invoke(null, result, httpPacket);
			return replyResult;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			if (e.getCause() instanceof HttpException) {
				// json格式的错误提醒
				HttpException exception = (HttpException) e.getCause();
				HttpPacket errorPacket = new HttpPacket(exception.getErrorType(), exception.getErrorData());
				httpPacket.putMonitor("服务器返回错误：错误号为：" + exception.getErrorData());
				return ReplyUtil.replyJson(errorPacket, httpPacket);
			} else {
				HttpConfig.log.error("HttpPacket,code为：" + httpPacket.gethOpCode() + "，IHttpListener为：" + (method != null ? method.getClass().getName() : "") + "处理失败", e);
				return false;
			}
		} catch (Exception e) {
			if (HttpConfig.log != null) {
				HttpConfig.log.error("HttpPacket,code为：" + httpPacket.gethOpCode() + "，IHttpListener为：" + (method != null ? method.getClass().getName() : "") + "处理失败", e);
			}
			return false;
		}
	}
}
