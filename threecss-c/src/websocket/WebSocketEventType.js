function WebSocketEventType() {
    // 链接完成
    this.CONNECTED = "connected";
    // 关闭
    this.CLOSE = "close";
    // 接到消息
    this.MESSAGE = "message";
    this.getMessage = function (wsOpCode) {
        return this.MESSAGE + "_" + wsOpCode;
    }
}
$T.webSocketEventType = new WebSocketEventType();