function Version() {
    this.NAME = "v1.0.0";
    this.addVersionToUrl = function (url) {
        var index = url.indexOf("?");
        if (index == -1) {
            url += "?v=" + this.NAME;
        } else {
            url += "&v=" + this.NAME;
        }
        return url;
    }
    this.addVersionAndTimeToUrl = function (url) {
        url = this.addVersionToUrl(url) + "&t=" + new Date().getTime();
        return url;
    }
    this.loadScript = function (script) {
        document.write("<script src='" + this.addVersionToUrl(script) + "' language='javascript' type='text/javascript'></script>");
    }
    this.loadCss = function (css) {
        document.write("<link href='" + this.addVersionToUrl(css) + "' rel='stylesheet' type='text/css'/>");
    }
    this.initLoad = function () {
        // 消息
        this.loadScript("js/threecss-c/mvc/Notification.js");
        this.loadScript("js/threecss-c/mvc/ViewManager.js");
        // http
        this.loadScript("js/threecss-c/http/HttpConfigNormal.js");
        this.loadScript("js/threecss-c/http/HttpConfig.js");
        this.loadScript("js/threecss-c/http/HttpResultFilter.js");
        this.loadScript("js/threecss-c/http/SendParamNormal.js");
        this.loadScript("js/threecss-c/http/SendParam.js");
        this.loadScript("js/threecss-c/http/TestFilter.js");

        // 工具
        this.loadScript("js/threecss-c/tools/ArrayTools.js");
        this.loadScript("js/threecss-c/display/DisplayObject.js");
        // 事件
        this.loadScript("js/threecss-c/event/Event.js");
        this.loadScript("js/threecss-c/event/EventPool.js");
        this.loadScript("js/threecss-c/event/EventDispatcher.js");
        // 动画
        this.loadScript("js/threecss-c/tween/TweenEventType.js");
        this.loadScript("js/threecss-c/tween/DelayedCall.js");
        this.loadScript("js/threecss-c/tween/DelayedCallPool.js");
        this.loadScript("js/threecss-c/tween/Transitions.js");
        this.loadScript("js/threecss-c/tween/Tween.js");
        this.loadScript("js/threecss-c/tween/TweenPool.js");
        this.loadScript("js/threecss-c/tween/Juggler.js");
        this.loadScript("js/threecss-c/tween/JugglerManager.js");

        this.loadScript("js/threecss-c/http/HttpUtilNormal.js");
        this.loadScript("js/threecss-c/http/HttpUtil.js");
        // 资源
        this.loadScript("js/threecss-c/resource/ResourceEventType.js");
        this.loadScript("js/threecss-c/resource/Loader.js");
        this.loadScript("js/threecss-c/resource/ResourceManager.js");
        // 注册
        this.loadScript("js/threecss-c/Register.js");
        // 模块
        this.loadScript("js/threecss-c/module/ModuleData.js");
        this.loadScript("js/threecss-c/module/ModuleManager.js");
        // websocket
        this.loadScript("js/threecss-c/websocket/WebSocketConfig.js");
        this.loadScript("js/threecss-c/websocket/WebSocketEventType.js");
        this.loadScript("js/threecss-c/websocket/WebSocketClient.js");

    }
    this.init = function (name) {
		if(name != null){
			this.NAME = name;
		}
    }

}
$T.version = new Version();