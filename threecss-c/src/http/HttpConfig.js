function HttpConfig() {
    this.TYPE_POST = "post";// 发送类型
    this.TYPE_GET = "get";// 发送类型
    this.RETURN_TYPE_JSON = "json";// 获取类型
    this.RETURN_TYPE_HTML = "html";// 获取类型
    // 允许消息头
    this.HOPCODE = "hOpCode";// 操作码
    this.TOKEN = "token";
    this.SEND_TYPE = "sendType";
    this.RECEIVE_TYPE = "receiveType";
    this.FILE_UUID = "fileUuid";// uuid
    this.PACKET = "packet";// 是文件类型的包
    // 发送类型
    this.SEND_TYPE_JSON = "sendTypeJson";
    this.SEND_TYPE_PROTOBUF = "sendTypeProtobuf";
    this.SEND_TYPE_PACKET = "sendTypePacket";
    this.SEND_TYPE_FILE_SAVE_SESSION = "sendTypeFileSaveSession";
    this.SEND_TYPE_FILE_NOT_SAVE = "sendTypeFileNotSave";
    this.SEND_TYPE_NONE = "sendTypeNone";
    // 接收类型
    this.RECEIVE_TYPE_JSON = "receiveTypeJson";
    this.RECEIVE_TYPE_PROTOBUF = "receiveTypeProtobuf";
    this.RECEIVE_TYPE_FILE = "receiveTypeFile";
    this.RECEIVE_TYPE_IMAGE = "receiveTypeImage";
    this.RECEIVCE_TYPE_STRING = "receiveTypeString";
    this.RECEIVE_TYPE_NONE = "receiveTypeNone";
}
$T.httpConfig = new HttpConfig();