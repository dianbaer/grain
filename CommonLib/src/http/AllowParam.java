package http;

public class AllowParam {
	// 操作码
	public static String HOPCODE = "hOpCode";
	// token值，标示用户身份
	public static String TOKEN = "token";
	// 文档类型
	public static String CONTENT_TYPE = "Content-Type";
	// 发送数据的类型，默认是json
	public static String SEND_TYPE = "sendType";
	// 接收数据的类型，默认是json
	public static String RECEIVE_TYPE = "receiveType";
	// 如果是文件类型并且需要进度，需要携带uuid
	public static String FILE_UUID = "fileUuid";
	// 如果是文件或者非（json、protobuf）取的数据包
	public static String PACKET = "packet";

	// 发送json
	public static String SEND_TYPE_JSON = "sendTypeJson";
	// 发送protobuf
	public static String SEND_TYPE_PROTOBUF = "sendTypeProtobuf";
	// 发送至packet是json
	public static String SEND_TYPE_PACKET = "sendTypePacket";
	// 发送文件存入session，数据存入packet是json
	public static String SEND_TYPE_FILE_SAVE_SESSION = "sendTypeFileSaveSession";
	// 发送文件操作函数自行处理，数据存入packet是json
	public static String SEND_TYPE_FILE_NOT_SAVE = "sendTypeFileNotSave";
	// 无数据
	public static String SEND_TYPE_NONE = "sendTypeNone";
	// 接收类型
	public static String RECEIVE_TYPE_JSON = "receiveTypeJson";
	public static String RECEIVE_TYPE_PROTOBUF = "receiveTypeProtobuf";
	public static String RECEIVE_TYPE_FILE = "receiveTypeFile";
	public static String RECEIVE_TYPE_IMAGE = "receiveTypeImage";
	public static String RECEIVCE_TYPE_STRING = "receiveTypeString";
	// 不接收返回数据
	public static String RECEIVE_TYPE_NONE = "receiveTypeNone";
	
	//接收除图片外的其他二进制流
	public static String RECEIVE_TYPE_OTHER_STREAM = "receiveTypeOtherStream";

}
