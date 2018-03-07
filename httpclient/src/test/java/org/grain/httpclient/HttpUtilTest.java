package org.grain.httpclient;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;

public class HttpUtilTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		HttpUtil.init("UTF-8", null);
	}

	@Test
	public void testGet() throws UnsupportedEncodingException {
		byte[] result = HttpUtil.send(null, "http://localhost:8080/", null, HttpUtil.GET);
		String str = new String(result, "UTF-8");
		System.out.println(str);
		assertEquals(true, str != null);
	}

	@Test
	public void testPost() throws UnsupportedEncodingException {
		String str = "{hOpCode: \"1\", userName: \"admin\", userPassword: \"123456\"}";
		byte[] result = HttpUtil.send(str, "http://localhost:8080/s", null, HttpUtil.POST);
		String returnStr = new String(result, "UTF-8");
		System.out.println(returnStr);
		assertEquals(true, str != null);
	}

	@Test
	public void testSendFile() throws UnsupportedEncodingException {
		String tokenId = "38bfecab619b48db8466ea7dd064f0d8";
		String str = "{hOpCode: \"1\", userName: \"dianbaer333\", userPassword: \"123456\"}";
		HashMap<String, String> headMap = new HashMap<String, String>();
		headMap.put("token", tokenId);
		headMap.put("packet", URLEncoder.encode(str, HttpUtil.ENCODE));
		File file = new File("D:\\github\\product\\grain\\httpclient\\src\\test\\resources\\k_nearest_neighbors.png");
		System.out.println(file.exists());
		byte[] result = HttpUtil.sendFile(file, "http://localhost:8080/s", headMap, HttpUtil.POST);
		String returnStr = new String(result, "UTF-8");
		System.out.println(returnStr);
		assertEquals(true, str != null);

	}

}
