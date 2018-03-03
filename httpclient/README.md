# grain-httpclient

grain-httpclient http客户端


此项目依赖

	grain-log
	commons-codec-1.10.jar
	commons-logging-1.2.jar
	httpclient-4.5.2.jar
	httpcore-4.4.6.jar
	httpmime-4.5.2.jar

使用

1、初始化

	HttpUtil.init("UTF-8", null);
	
2、发送GET请求

	byte[] result = HttpUtil.send(null, "http://172.27.108.74:8080/UCenter", null, HttpUtil.GET);
	String str = new String(result, "UTF-8");
	
3、发送POST请求

	String str = "{hOpCode: 20, userName: \"admin\", userPassword: \"123456\"}";
	HashMap<String, String> headMap = new HashMap<String, String>();
	headMap.put("hOpCode", "20");
	headMap.put("sendType", "sendTypeJson");
	headMap.put("receiveType", "receiveTypeJson");
	byte[] result = HttpUtil.send(str, "http://172.27.108.74:8080/UCenter/s", headMap, HttpUtil.POST);
	String returnStr = new String(result, "UTF-8");
	
4、发送文件

	String tokenId = "38bfecab619b48db8466ea7dd064f0d8";
	String str = "{hOpCode: 10, userName: \"dianbaer333\", userPassword: \"123456\"}";
	HashMap<String, String> headMap = new HashMap<String, String>();
	headMap.put("hOpCode", "10");
	headMap.put("sendType", "sendTypeFileSaveSession");
	headMap.put("receiveType", "receiveTypeJson");
	headMap.put("token", tokenId);
	headMap.put("packet", URLEncoder.encode(str, HttpUtil.ENCODE));
	File file = new File("C:\\Users\\admin\\Desktop\\github\\grain\\trunk\\grain-httpclient\\src\\test\\resources\\k_nearest_neighbors.png");
	byte[] result = HttpUtil.sendFile(file, "http://172.27.108.74:8080/UCenter/s", headMap, HttpUtil.POST);
	String returnStr = new String(result, "UTF-8");

