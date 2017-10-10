function View1Mediator() {

    this.init = function (view) {
        var sendDemoTest = document.getElementById("sendDemoTest");
        sendDemoTest.addEventListener("click", this.onSendDemoTest);
    }
    // 注销方法
    this.dispose = function () {

    }
    // 关心消息数组
    this.listNotificationInterests = [];
    // 关心的消息处理
    this.handleNotification = function (data) {

    }
    this.onSendDemoTest = function (e) {
       
    }
}
$T.view1Mediator = new View1Mediator();