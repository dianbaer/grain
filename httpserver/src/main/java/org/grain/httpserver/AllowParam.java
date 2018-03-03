package org.grain.httpserver;

public class AllowParam {
	// 关键字--操作码，优先级from>param>header
	public static String HOPCODE = "hOpCode";
	// 关键字--token值，标示用户身份，优先级from>param>header
	public static String TOKEN = "token";
	// 发送内容的类型
	public static String CONTENT_TYPE = "Content-Type";
	// 关键字-请求的唯一标示，一般用于异步访问文件上传进度，优先级from>param>header
	public static String FILE_UUID = "fileUuid";
	// 关键字--消息包，如果含有packet说明是完整的，其他的参数忽略，优先级from>param>header
	public static String PACKET = "packet";

}
