package http;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import log.LogManager;
import net.sf.json.JSONObject;

public class HttpUtil {
	public static SSLConnectionSocketFactory sslSocketFactory;

	public static void init(String sendCode, String encode) throws Exception {
		HttpConfig.SEND_CODE = sendCode;
		HttpConfig.ENCODE = encode;
		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustStrategyAll()).build();
		sslSocketFactory = new SSLConnectionSocketFactory(sslContext);
	}

	public static HttpPacket send(HttpPacket httpPacket, String url, String sendType, String receiveType, String token) {

		HashMap<String, String> headMap = new HashMap<String, String>();
		headMap.put(AllowParam.HOPCODE, String.valueOf(httpPacket.gethOpCode()));
		headMap.put(AllowParam.SEND_TYPE, sendType);
		headMap.put(AllowParam.RECEIVE_TYPE, receiveType);
		if (token != null && !token.equals("")) {
			headMap.put(AllowParam.TOKEN, token);
		}
		String data = null;
		if (sendType.equals(AllowParam.SEND_TYPE_JSON)) {
			data = CodeUtils.encodeJson(httpPacket);
		} else if (sendType.equals(AllowParam.SEND_TYPE_PROTOBUF)) {
			data = CodeUtils.encodeProtoBuf(httpPacket);
		} else {
			LogManager.httpLog.warn("httpUtil不支持发送这种类型：" + sendType);
			return null;
		}
		if (data == null) {
			LogManager.httpLog.warn("序列化返回的字符串为空,请注意");
			return null;
		}
		byte[] result = sendNormal(data, url, headMap);
		if (result == null) {
			return null;
		}
		HttpPacket resultHttpPacket = null;
		if (receiveType.equals(AllowParam.RECEIVE_TYPE_JSON)) {
			resultHttpPacket = CodeUtils.decodeJson(result, false);
		} else if (receiveType.equals(AllowParam.RECEIVE_TYPE_PROTOBUF)) {
			resultHttpPacket = CodeUtils.decodeProtoBuf(result, false);
		} else {
			LogManager.httpLog.warn("httpUtil不支持这种返回类型解析" + receiveType);
			return null;
		}
		LogManager.httpLog.info(resultHttpPacket.gethOpCode() + "");
		return resultHttpPacket;

	}

	public static JSONObject sendJson(JSONObject data, String url, Map<String, String> headMap) {
		try {
			String dataStr = null;
			if (data != null) {
				dataStr = data.toString();
			}
			byte[] result = sendNormal(dataStr, url, headMap);
			if (result == null) {
				return null;
			}
			String jsonStr = new String(result, HttpConfig.ENCODE);
			return JSONObject.fromObject(jsonStr);
		} catch (Exception e) {
			LogManager.httpLog.error("http请求异常", e);
			return null;
		}

	}

	public static byte[] sendNormal(String data, String url, Map<String, String> headMap) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse httpResponse = null;
		try {
			if (url.startsWith("https")) {
				httpClient = HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();
			} else {
				httpClient = HttpClients.createDefault();
			}

			HttpPost httpPost = new HttpPost(url);
			if (headMap != null) {
				Object[] keyArray = headMap.keySet().toArray();
				for (int i = 0; i < keyArray.length; i++) {
					String key = String.valueOf(keyArray[i]);
					String value = headMap.get(key);
					httpPost.addHeader(key, value);
				}
			}
			if (data != null && !data.equals("")) {
				HttpEntity entity = new StringEntity(data, ContentType.create(HttpConfig.CONTENT_TYPE_JSON, HttpConfig.ENCODE));
				httpPost.setEntity(entity);
			}
			httpResponse = httpClient.execute(httpPost);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity responseEntity = httpResponse.getEntity();
				if (responseEntity != null) {
					byte[] result = EntityUtils.toByteArray(responseEntity);
					return result;
				} else {
					LogManager.httpLog.warn("responseEntity为空");
					return null;
				}
			} else {
				LogManager.httpLog.warn("http返回码为：" + statusCode + "请注意");
				return null;
			}

		} catch (Exception e) {
			LogManager.httpLog.error("http请求异常", e);
			return null;
		} finally {
			try {
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				LogManager.httpLog.error("关闭httpClient异常", e);
			}
			try {
				if (httpResponse != null) {
					httpResponse.close();
				}
			} catch (IOException e) {
				LogManager.httpLog.error("关闭httpResponse异常", e);
			}
		}

	}

	public static HttpPacket sendFile(File file, HttpPacket httpPacket, String url, String sendType, String receiveType, String fileUuid, String token) {
		try {
			HashMap<String, String> headMap = new HashMap<String, String>();
			headMap.put(AllowParam.HOPCODE, String.valueOf(httpPacket.gethOpCode()));
			headMap.put(AllowParam.SEND_TYPE, sendType);
			headMap.put(AllowParam.RECEIVE_TYPE, receiveType);

			if (token != null && !token.equals("")) {
				headMap.put(AllowParam.TOKEN, token);
			}
			String data = CodeUtils.encodeJson(httpPacket);
			if (data == null) {
				LogManager.httpLog.warn("序列化返回的字符串为空,请注意");
				return null;
			}
			headMap.put(AllowParam.PACKET, URLEncoder.encode(data, HttpConfig.ENCODE));

			if (sendType.equals(AllowParam.SEND_TYPE_FILE_SAVE_SESSION) && fileUuid != null) {
				headMap.put(AllowParam.FILE_UUID, fileUuid);
			}
			byte[] result = sendFileNormal(file, data, url, headMap);
			if (result == null) {
				return null;
			}
			HttpPacket resultHttpPacket = null;
			if (receiveType.equals(AllowParam.RECEIVE_TYPE_JSON)) {
				resultHttpPacket = CodeUtils.decodeJson(result, false);
			} else if (receiveType.equals(AllowParam.RECEIVE_TYPE_PROTOBUF)) {
				resultHttpPacket = CodeUtils.decodeProtoBuf(result, false);
			} else {
				LogManager.httpLog.warn("httpUtil不支持这种返回类型解析" + receiveType);
				return null;
			}
			LogManager.httpLog.info(resultHttpPacket.gethOpCode() + "");
			return resultHttpPacket;
		} catch (Exception e) {
			LogManager.httpLog.error("http请求异常", e);
			return null;
		}
	}

	public static byte[] sendFileNormal(File file, String data, String url, Map<String, String> headMap) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse httpResponse = null;
		try {
			if (url.startsWith("https")) {
				httpClient = HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();
			} else {
				httpClient = HttpClients.createDefault();
			}
			HttpPost httpPost = new HttpPost(url);
			if (headMap != null) {
				Object[] keyArray = headMap.keySet().toArray();
				for (int i = 0; i < keyArray.length; i++) {
					String key = String.valueOf(keyArray[i]);
					String value = headMap.get(key);
					httpPost.addHeader(key, value);
				}
			}

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addPart("file", new FileBody(file));
			httpPost.setEntity(builder.build());

			httpResponse = httpClient.execute(httpPost);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity responseEntity = httpResponse.getEntity();
				if (responseEntity != null) {
					byte[] result = EntityUtils.toByteArray(responseEntity);
					return result;
				} else {
					LogManager.httpLog.warn("responseEntity为空");
					return null;
				}
			} else {
				LogManager.httpLog.warn("http返回码为：" + statusCode + "请注意");
				return null;
			}

		} catch (Exception e) {
			LogManager.httpLog.error("http请求异常", e);
			return null;
		} finally {
			try {
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				LogManager.httpLog.error("关闭httpClient异常", e);
			}
			try {
				if (httpResponse != null) {
					httpResponse.close();
				}
			} catch (IOException e) {
				LogManager.httpLog.error("关闭httpResponse异常", e);
			}
		}

	}

	public static String getRequestUrl(HttpPacket httpPacket, String url, String token, String sendType, String receiveType) {
		String packet;
		try {
			packet = URLEncoder.encode(CodeUtils.encodeJson(httpPacket), HttpConfig.ENCODE);
		} catch (UnsupportedEncodingException e) {
			LogManager.httpLog.error("encode pakcet异常", e);
			return null;
		}
		return url + "?" + AllowParam.HOPCODE + "=" + httpPacket.gethOpCode() + "&" + AllowParam.TOKEN + "=" + token + "&" + AllowParam.SEND_TYPE + "=" + sendType + "&" + AllowParam.RECEIVE_TYPE + "=" + receiveType + "&" + AllowParam.PACKET + "=" + packet;
	}
}
