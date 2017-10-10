function IndexMediator() {

	this.webSocketClient = null;
    this.init = function (view) {

        // 模块
        $T.moduleManager.loadModule("html/top.html", document.getElementById("index_top"), null, $T.topMediator);
        $T.moduleManager.loadModule("html/left.html", document.getElementById("index_left"), null, $T.leftMediator);
        $T.moduleManager.loadModule("html/body.html", document.getElementById("index_body"), null, $T.bodyMediator);
        $T.moduleManager.loadModule("html/bottom.html", document.getElementById("index_bottom"), null, $T.bottomMediator);
        
        this.webSocketClient = new WebSocketClient();
        this.webSocketClient.WebSocketClient($T.url.url);
        this.webSocketClient.addEventListener($T.webSocketEventType.CONNECTED, this.onConnected, this);
        this.webSocketClient.addEventListener($T.webSocketEventType.CLOSE, this.onClose, this);
		this.webSocketClient.addEventListener($T.webSocketEventType.getMessage("tests"), this.onTestS, this);
    }
    // 注销方法
    this.dispose = function () {


    }
    this.onConnected = function(){
    	var data = {
            "wsOpCode": "testc",
            "msg": "你好啊，服务器"
        };
        this.webSocketClient.send(data);
    }
    this.onClose = function(){
    	
    }
    this.onTestS = function(event){
    	var data = event.mData;
    	alert("服务器返回"+data.msg);
    }
    // 关心消息数组
    this.listNotificationInterests = [$T.notification.SEND_HTTP_START, $T.notification.SEND_HTTP_END, $T.notification.SYSTEM_ERROR];
    // 关心的消息处理
    this.handleNotification = function (data) {

    }
    this.advanceTime = function (passedTime) {

    }

}
$T.indexMediator = new IndexMediator();