package http;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import log.LogManager;

public class HttpUtil {
	public static void init(String sendCode, String encode) {
		HttpConfig.SEND_CODE = sendCode;
		HttpConfig.ENCODE = encode;
	}

	public static HttpPacket send(HttpPacket httpPacket, String url, String sendType, String receiveType, String token) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse httpResponse = null;
		try {
			httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader(AllowParam.HOPCODE, String.valueOf(httpPacket.gethOpCode()));
			httpPost.addHeader(AllowParam.SEND_TYPE, sendType);
			httpPost.addHeader(AllowParam.RECEIVE_TYPE, receiveType);
			if (token != null && !token.equals("")) {
				httpPost.addHeader(AllowParam.TOKEN, token);
			}
			String sendStr = null;
			if (sendType.equals(AllowParam.SEND_TYPE_JSON)) {
				sendStr = CodeUtils.encodeJson(httpPacket);
			} else if (sendType.equals(AllowParam.SEND_TYPE_PROTOBUF)) {
				sendStr = CodeUtils.encodeProtoBuf(httpPacket);
			} else {
				LogManager.httpLog.warn("httpUtil不支持发送这种类型：" + sendType);
				return null;
			}
			if (sendStr == null) {
				LogManager.httpLog.warn("序列化返回的字符串为空,请注意");
				return null;
			}
			HttpEntity entity = new StringEntity(sendStr, ContentType.create(HttpConfig.CONTENT_TYPE_JSON, HttpConfig.ENCODE));
			httpPost.setEntity(entity);
			httpResponse = httpClient.execute(httpPost);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity responseEntity = httpResponse.getEntity();
				if (responseEntity != null) {
					byte[] result = EntityUtils.toByteArray(responseEntity);
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
				httpClient.close();
			} catch (IOException e) {
				LogManager.httpLog.error("关闭httpClient异常", e);
			}
			try {
				httpResponse.close();
			} catch (IOException e) {
				LogManager.httpLog.error("关闭httpResponse异常", e);
			}
		}

	}

	public static HttpPacket sendFile(File file, HttpPacket httpPacket, String url, String sendType, String receiveType, String fileUuid, String token) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse httpResponse = null;
		try {
			httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader(AllowParam.HOPCODE, String.valueOf(httpPacket.gethOpCode()));
			httpPost.addHeader(AllowParam.SEND_TYPE, sendType);
			httpPost.addHeader(AllowParam.RECEIVE_TYPE, receiveType);
			if (token != null && !token.equals("")) {
				httpPost.addHeader(AllowParam.TOKEN, token);
			}
			String sendStr = CodeUtils.encodeJson(httpPacket);
			if (sendStr == null) {
				LogManager.httpLog.warn("序列化返回的字符串为空,请注意");
				return null;
			}
			httpPost.addHeader(AllowParam.PACKET, URLEncoder.encode(sendStr, HttpConfig.ENCODE));
			if (sendType.equals(AllowParam.SEND_TYPE_FILE_SAVE_SESSION) && fileUuid != null) {
				httpPost.addHeader(AllowParam.FILE_UUID, fileUuid);
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
				httpClient.close();
			} catch (IOException e) {
				LogManager.httpLog.error("关闭httpClient异常", e);
			}
			try {
				httpResponse.close();
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
