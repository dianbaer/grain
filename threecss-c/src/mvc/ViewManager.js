function ViewManager() {
    this.observerMap = [];
    this.viewMap = [];
    /**
     * 注册监听函数
     */
    this.registerObserver = function (notificationName, observer) {
        if (notificationName == null || observer == null) {
            alert("注册消息或控制器为空，请检查");
            return;
        }
        var observers = this.observerMap[notificationName];
        if (observers) {
            var isHave = false;
            for (var i = 0; i < observers.length; i++) {
                if (observers[i] == observer) {
                    isHave = true;
                    break;
                }
            }
            if (!isHave) {
                observers.push(observer);
            } else {
                alert("重复注册消息监听,请检查逻辑");
            }
        } else {
            this.observerMap[notificationName] = [observer];
        }
    }
    this.registerObserverArray = function (notificationNameArray, observer) {
        for (var i = 0; i < notificationNameArray.length; i++) {
            this.registerObserver(notificationNameArray[i], observer);
        }
    }
    this.getNotification = function (name, body) {
        var obj = {};
        obj.name = name;
        obj.body = body;
        return obj;
    }
    /**
     * 发消息给监听函数
     */
    this.notifyObservers = function (notification) {
        var obj = this.observerMap[notification.name];
        if (obj) {
            // 这里最好复制这个数组，防止回调的时候，在注册新的监听
            var observers = obj.concat();
            for (var i = 0; i < observers.length; i++) {
                var observer = observers[i];
                observer.handleNotification.call(observer, [notification]);
            }
        }
    }
    this.removeObserverArray = function (notificationNameArray, observer) {
        for (var i = 0; i < notificationNameArray.length; i++) {
            this.removeObserver(notificationNameArray[i], observer);
        }
    }
    /**
     * 移除
     */
    this.removeObserver = function (notificationName, observer) {
        if (notificationName == null) {
            return;
        }
        var observers = this.observerMap[notificationName];
        var isHave = false;
        for (var i = 0; i < observers.length; i++) {
            if (observers[i] == observer) {
                observers.splice(i, 1);
                isHave = true;
                break;
            }
        }
        if (!isHave) {
            alert("移除消息监听失败，请检查逻辑");
        }
        if (observers.length == 0) {
            delete this.observerMap[notificationName];
        }
    }
    /**
     * 注册视图
     */
    this.registerView = function (viewName, view) {
        this.viewMap[viewName] = view;
    }
    /**
     * 获取视图
     */
    this.retrieveView = function (viewName) {
        return this.viewMap[viewName];
    }
    /**
     * 移除视图
     */
    this.removeView = function (viewName) {
        var view = this.viewMap[viewName];
        if (view != null) {
            delete this.viewMap[viewName];
        }
        return view;
    }
    this.reset = function () {
        // 清空所有注册消息
        this.observerMap = [];
        // 清空所有视图
        this.viewMap = [];
    }
}
$T.viewManager = new ViewManager();