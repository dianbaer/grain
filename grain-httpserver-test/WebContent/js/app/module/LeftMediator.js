function LeftMediator() {
    this.view1;
    this.view2;
    this.init = function (view) {
        this.view1 = document.getElementById("view1");
        this.view2 = document.getElementById("view2");
        this.view1.addEventListener("click", this.onView1Click);
        this.view2.addEventListener("click", this.onView2Click);

    }
    // 注销方法
    this.dispose = function () {
        this.view1.removeEventListener("click", this.onView1Click);
        this.view2.removeEventListener("click", this.onView2Click);
    }
    // 关心消息数组
    this.listNotificationInterests = [];
    // 关心的消息处理
    this.handleNotification = function (data) {

    }
    this.onView1Click = function (e) {
        $T.viewManager.notifyObservers($T.viewManager.getNotification($T.notificationExt.CHANGE_BODY, "view1"));
    }
    this.onView2Click = function (e) {
        $T.viewManager.notifyObservers($T.viewManager.getNotification($T.notificationExt.CHANGE_BODY, "view2"));
    }
}
$T.leftMediator = new LeftMediator();