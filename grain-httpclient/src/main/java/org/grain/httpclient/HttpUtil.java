package org.grain.httpclient;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.grain.log.ILog;

public class HttpUtil {
	public static SSLConnectionSocketFactory sslSocketFactory;

	public static String ENCODE;
	private static ILog log;
	private static String CONTENT_TYPE_JSON = "application/json";

	public final static String GET = "get";

	public final static String PUT = "put";

	public final static String DELETE = "delete";

	public final static String HEAD = "head";

	public final static String POST = "post";

	public final static String PATCH = "patch";

	/**
	 * 初始化
	 * 
	 * @param encode
	 *            编码
	 * @param log
	 *            日志可以为null
	 * @throws Exception
	 */
	public static void init(String encode, ILog log) throws Exception {
		HttpUtil.ENCODE = encode;
		HttpUtil.log = log;
		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustStrategyAll()).build();
		sslSocketFactory = new SSLConnectionSocketFactory(sslContext);
	}

	/**
	 * 发送请求
	 * 
	 * @param data
	 *            数据
	 * @param url
	 *            地址
	 * @param headMap
	 *            头消息map
	 * @param type
	 *            类型
	 * @return
	 */
	public static byte[] send(String data, String url, Map<String, String> headMap, String type) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse httpResponse = null;
		try {
			if (url.startsWith("https")) {
				httpClient = HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();
			} else {
				httpClient = HttpClients.createDefault();
			}
			HttpEntity entity = null;
			HttpRequestBase http = null;
			switch (type) {
			case GET:
				http = new HttpGet(url);
				break;
			case PUT:
				http = new HttpPut(url);
				if (data != null && !data.equals("")) {
					entity = new ByteArrayEntity(data.getBytes(ENCODE), ContentType.create(CONTENT_TYPE_JSON, ENCODE));
					((HttpEntityEnclosingRequestBase) http).setEntity(entity);
				}
				break;
			case PATCH:
				http = new HttpPatch(url);
				if (data != null && !data.equals("")) {
					entity = new ByteArrayEntity(data.getBytes(ENCODE), ContentType.create(CONTENT_TYPE_JSON, ENCODE));
					((HttpEntityEnclosingRequestBase) http).setEntity(entity);
				}
				break;
			case DELETE:
				http = new HttpDelete(url);
				break;
			case HEAD:
				http = new HttpHead(url);
				break;
			case POST:
				http = new HttpPost(url);
				if (data != null && !data.equals("")) {
					entity = new ByteArrayEntity(data.getBytes(ENCODE), ContentType.create(CONTENT_TYPE_JSON, ENCODE));
					((HttpEntityEnclosingRequestBase) http).setEntity(entity);
				}
				break;
			}
			if (headMap != null) {
				Object[] keyArray = headMap.keySet().toArray();
				for (int i = 0; i < keyArray.length; i++) {
					String key = String.valueOf(keyArray[i]);
					String value = headMap.get(key);
					http.addHeader(key, value);
				}
			}

			httpResponse = httpClient.execute(http);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity responseEntity = httpResponse.getEntity();
				if (responseEntity != null) {
					byte[] result = EntityUtils.toByteArray(responseEntity);
					return result;
				} else {
					if (log != null) {
						log.warn("responseEntity为空");
					}
					return null;
				}
			} else {
				if (log != null) {
					log.warn("http返回码为：" + statusCode + "请注意");
				}
				return null;
			}

		} catch (Exception e) {
			if (log != null) {
				log.error("http请求异常", e);
			}
			return null;
		} finally {
			try {
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				if (log != null) {
					log.error("关闭httpClient异常", e);
				}
			}
			try {
				if (httpResponse != null) {
					httpResponse.close();
				}
			} catch (IOException e) {
				if (log != null) {
					log.error("关闭httpResponse异常", e);
				}
			}
		}

	}

	/**
	 * 发送文件
	 * 
	 * @param file
	 *            文件
	 * @param url
	 *            地址
	 * @param headMap
	 *            头消息map
	 * @param type
	 *            类型
	 * @return
	 */
	public static byte[] sendFile(File file, String url, Map<String, String> headMap, String type) {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse httpResponse = null;
		try {
			if (url.startsWith("https")) {
				httpClient = HttpClients.custom().setSSLSocketFactory(sslSocketFactory).build();
			} else {
				httpClient = HttpClients.createDefault();
			}
			HttpRequestBase http = null;
			switch (type) {
			case PUT:
				http = new HttpPut(url);
				break;
			case PATCH:
				http = new HttpPatch(url);
				break;
			case POST:
				http = new HttpPost(url);
				break;
			}

			if (headMap != null) {
				Object[] keyArray = headMap.keySet().toArray();
				for (int i = 0; i < keyArray.length; i++) {
					String key = String.valueOf(keyArray[i]);
					String value = headMap.get(key);
					http.addHeader(key, value);
				}
			}

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addPart("file", new FileBody(file));
			((HttpEntityEnclosingRequestBase) http).setEntity(builder.build());

			httpResponse = httpClient.execute(http);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				HttpEntity responseEntity = httpResponse.getEntity();
				if (responseEntity != null) {
					byte[] result = EntityUtils.toByteArray(responseEntity);
					return result;
				} else {
					if (log != null) {
						log.warn("responseEntity为空");
					}
					return null;
				}
			} else {
				if (log != null) {
					log.warn("http返回码为：" + statusCode + "请注意");
				}
				return null;
			}

		} catch (Exception e) {
			if (log != null) {
				log.error("http请求异常", e);
			}
			return null;
		} finally {
			try {
				if (httpClient != null) {
					httpClient.close();
				}
			} catch (IOException e) {
				if (log != null) {
					log.error("关闭httpClient异常", e);
				}
			}
			try {
				if (httpResponse != null) {
					httpResponse.close();
				}
			} catch (IOException e) {
				if (log != null) {
					log.error("关闭httpResponse异常", e);
				}
			}
		}

	}
}
