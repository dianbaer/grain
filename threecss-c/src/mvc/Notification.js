function Notification() {
    this.SEND_HTTP_START = "SEND_HTTP_START";// 发送http请求
    this.SEND_HTTP_END = "SEND_HTTP_END";// 发送http请求完成
    this.SYSTEM_ERROR = "SYSTEM_ERROR";// 系统内部错误
    this.MODULE_INIT_COMPLETE = "moduleInitComplete";// 模块初始化完成
}
$T.notification = new Notification();