# grain-httpserver

grain-httpserver http服务器

### 设计思路为：


定义关键字并统筹所有请求参数，进行数据格式化。支持文件与操作数据的隔离。支持post表单数据与json数据，支持表单文件，支持get拼接参数，支持扩展消息包过滤器，支持扩展请求回复类型。


### 关键字：


	hOpCode---操作码优先级from > param > header
	token-----token值，优先级 from > param > header
	fileUuid---请求的唯一标示，一般用于异步访问文件上传进度，优先级from > param > header
	packet-----消息包，如果含有packet说明是完整的，其他的参数忽略，优先级from > param > header
	
### 解析优先级：


	表单解析含packet > 表单解析与param进行参数拼接 > post的json解析 > param含packet > header含packet > get进行param参数拼接
	



### 此项目依赖:

	grain-log
	commons-beanutils-1.9.3.jar
	commons-collections-3.2.2.jar
	commons-fileupload-1.3.2.jar
	commons-io-2.5.jar
	commons-lang-2.6.jar
	commons-logging-1.2.jar
	ezmorph-1.0.6.jar
	javax.servlet-api-3.1.0.jar
	json-lib-2.4-jdk15.jar
	protobuf-java-3.1.0.jar
	protobuf-java-format-1.4.jar

### 使用

>1、配置web.xml


	<!-- 初始化servlet -->
	<servlet>
		<servlet-name>InitHttpServer</servlet-name>
		<servlet-class>org.grain.httpserver.InitHttpServer</servlet-class>
		<load-on-startup>0</load-on-startup>
	</servlet>
	<!-- 配置映射 -->
	<servlet-mapping>
		<servlet-name>InitHttpServer</servlet-name>
		<url-pattern>/s</url-pattern>
	</servlet-mapping>
	<!-- 扩展类，服务器启动时自动初始化这个类 -->
	<context-param>
		<param-name>Expand</param-name>
		<param-value>server.ExpandServer</param-value>
	</context-param>
	<!-- 日志对象，需要传进日志对象grain-httpserver才能打印日志 -->
	<context-param>
		<param-name>ILog</param-name>
		<param-value>test.GrainLog</param-value>
	</context-param>
	<!-- 上传文件时进度实现类，一般用于存储上传进度 -->
	<context-param>
		<param-name>IUploadProgress</param-name>
		<param-value>test.UploadProgressListener</param-value>
	</context-param>
	
	
>2、编写扩展类

TestHttpService----关注http请求的消息

	package test;
	import java.io.File;
	import java.io.IOException;
	import java.util.HashMap;
	import java.util.Map;
	import javax.websocket.EncodeException;
	import org.grain.httpserver.HttpConfig;
	import org.grain.httpserver.HttpPacket;
	import org.grain.httpserver.IHttpListener;
	import org.grain.httpserver.ReplyFile;
	import org.grain.httpserver.ReplyImage;
	import org.grain.httpserver.ReplyString;
	import protobuf.http.UserGroupProto.GetTokenS;
	public class TestHttpService implements IHttpListener {
	
		@Override
		public Map<String, String> getHttps() {
			HashMap<String, String> map = new HashMap<>();
			map.put("1", "onTestC");
			map.put("2", "onFileC");
			map.put("3", "onImageC");
			map.put("4", "onStringC");
			map.put("5", "onReplyStringC");
			return map;
		}
		//返回json型
		public HttpPacket onTestC(HttpPacket httpPacket) throws IOException, EncodeException {
			GetTokenS.Builder builder = GetTokenS.newBuilder();
			builder.setHOpCode(httpPacket.gethOpCode());
			builder.setTokenId("111111");
			builder.setTokenExpireTime("222222");
			HttpPacket packet = new HttpPacket(httpPacket.gethOpCode(), builder.build());
			return packet;
		}
		//返回文件类型
		public ReplyFile onFileC(HttpPacket httpPacket) throws IOException, EncodeException {
			File file = new File(HttpConfig.PROJECT_PATH + "/" + HttpConfig.PROJECT_NAME + "/k_nearest_neighbors.png");
			ReplyFile replyFile = new ReplyFile(file, "你好.png");
			return replyFile;
		}
		//返回图片类型
		public ReplyImage onImageC(HttpPacket httpPacket) throws IOException, EncodeException {
			File file = new File(HttpConfig.PROJECT_PATH + "/" + HttpConfig.PROJECT_NAME + "/k_nearest_neighbors.png");
			ReplyImage image = new ReplyImage(file);
			return image;
		}
		//返回字符串
		public String onStringC(HttpPacket httpPacket) throws IOException, EncodeException {
			return "<html><head></head><body><h1>xxxxxxxxxxxx<h1></body></html>";
		}
		//返回字符串并携带头消息
		public ReplyString onReplyStringC(HttpPacket httpPacket) throws IOException, EncodeException {
			String str = "<html><head></head><body><h1>xxxxxxxxxxxx<h1></body></html>";
			ReplyString replyString = new ReplyString(str, "text/html");
			return replyString;
		}
	}
	
ExpandServer-----------启动时加载的扩展类
	
	package server;
	import org.grain.httpserver.HttpManager;
	import protobuf.http.UserGroupProto.GetTokenC;
	import protobuf.http.UserGroupProto.GetTokenS;
	import test.TestHttpService;
	public class ExpandServer {
		public ExpandServer() throws Exception {
			//设置对应的操作码与映射类
			HttpManager.addMapping("1", GetTokenC.class, GetTokenS.class);
			HttpManager.addMapping("2", GetTokenC.class, GetTokenS.class);
			HttpManager.addMapping("3", GetTokenC.class, GetTokenS.class);
			HttpManager.addMapping("4", GetTokenC.class, GetTokenS.class);
			HttpManager.addMapping("5", GetTokenC.class, GetTokenS.class);
			//注册关注的消息
			TestHttpService testHttpService = new TestHttpService();
			HttpManager.addHttpListener(testHttpService);
		}
	}
	
>3、访问

	json格式
	
	http://localhost:8080/grain-httpserver-test/s?hOpCode=1&userName=xxx&userPassword=xxx
	文件下载
	
	http://localhost:8080/grain-httpserver-test/s?hOpCode=2&userName=xxx&userPassword=xxx
	图片显示
	
	http://localhost:8080/grain-httpserver-test/s?hOpCode=3&userName=xxx&userPassword=xxx
	字符串
	
	http://localhost:8080/grain-httpserver-test/s?hOpCode=4&userName=xxx&userPassword=xxx
	字符串并且设定相应的头消息
	
	http://localhost:8080/grain-httpserver-test/s?hOpCode=5&userName=xxx&userPassword=xxx
	
>4、可以接收处理的方式

	1、Get请求URL形式例如：
	
		http://localhost:8080/grain-httpserver-test/s?hOpCode=1&userName=xxx&userPassword=xxx
		
	2、post From表单支持携带文件
	
	3、post json数据
	