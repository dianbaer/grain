function BodyMediator() {

    this.init = function (view) {
        $T.moduleManager.loadModule("html/view1.html", document.getElementById("body"), "view", $T.view1Mediator);

    }
    // 注销方法
    this.dispose = function () {

    }
    // 关心消息数组
    this.listNotificationInterests = [$T.notificationExt.CHANGE_BODY];
    // 关心的消息处理
    this.handleNotification = function (data) {
        switch (data[0].name) {
            case $T.notificationExt.CHANGE_BODY:
                if (data[0].body == "view1") {
                    $T.moduleManager.loadModule("html/view1.html", document.getElementById("body"), "view", $T.view1Mediator);
                } else if (data[0].body == "view2") {
                    $T.moduleManager.loadModule("html/view2.html", document.getElementById("body"), "view", $T.view2Mediator);
                }

                break;
        }
    }
}
$T.bodyMediator = new BodyMediator();