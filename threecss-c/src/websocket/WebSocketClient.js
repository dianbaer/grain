function WebSocketClient() {
    this.webSocket;
    this.isConnected = false;
    this.url;
    this.WebSocketClient = function (url) {
        this.url = url;
        this.connect();
        EventDispatcher.apply(this);
    }
    this.connect = function () {
        this.webSocket = new WebSocket(this.url);
        this.onOpenListener(this, this.onOpen);
        this.onCloseListener(this, this.onClose);
        this.onErrorListener(this, this.onError);
        this.onMessageListener(this, this.onMessage);
    }
    this.onOpenListener = function (webSocketClient, call) {
        var callFunc = function (event) {
            call.call(webSocketClient, event);
        }
        webSocketClient.webSocket.onopen = callFunc;

    }
    this.onCloseListener = function (webSocketClient, call) {
        var callFunc = function (event) {
            call.call(webSocketClient, event);
        }
        webSocketClient.webSocket.onclose = callFunc;

    }
    this.onErrorListener = function (webSocketClient, call) {
        var callFunc = function (event) {
            call.call(webSocketClient, event);
        }
        webSocketClient.webSocket.onerror = callFunc;

    }
    this.onMessageListener = function (webSocketClient, call) {
        var callFunc = function (event) {
            call.call(webSocketClient, event);
        }
        webSocketClient.webSocket.onmessage = callFunc;

    }
    this.onOpen = function (event) {
        this.isConnected = true;
        this.dispatchEventWith($T.webSocketEventType.CONNECTED);
    }
    this.send = function (data) {
        if (!this.isConnected) {
            alert("未链接至websocket服务器");
            return;
        }
        var blob = new Blob([JSON.stringify(data)]);
        this.webSocket.send(blob);
    }
    this.onMessage = function (event) {
        var data = eval('(' + event.data + ')');
        if (data[$T.webSocketConfig.WSOPCODE] == null) {
            return;
        }
        this.dispatchEventWith($T.webSocketEventType.getMessage(data[$T.webSocketConfig.WSOPCODE]), false, data);
    }
    this.onClose = function (event) {
        this.isConnected = false;
        this.dispatchEventWith($T.webSocketEventType.CLOSE);
    }
    this.onError = function (event) {

    }
    this.close = function () {
        this.webSocket.close();
    }
}
