function IndexMediator() {

    this.init = function (view) {

        // 模块
        $T.moduleManager.loadModule("html/top.html", document.getElementById("index_top"), null, $T.topMediator);
        $T.moduleManager.loadModule("html/left.html", document.getElementById("index_left"), null, $T.leftMediator);
        $T.moduleManager.loadModule("html/body.html", document.getElementById("index_body"), null, $T.bodyMediator);
        $T.moduleManager.loadModule("html/bottom.html", document.getElementById("index_bottom"), null, $T.bottomMediator);

        // 动画
        var display = new DisplayObject();
        display.DisplayObject(document.getElementById("tween_div"));
        display.setAlpha(1);
        var tween = new Tween();
        tween.Tween(display, 1, $T.transitions.EASE_OUT_BACK);
        tween.animate(display.getX, display.setX, 900);
        tween.animate(display.getY, display.setY, 500);
        $T.jugglerManager.oneJuggler.add(tween);
    }
    // 注销方法
    this.dispose = function () {


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