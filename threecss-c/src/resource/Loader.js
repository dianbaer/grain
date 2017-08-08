function Loader() {
    this.url;
    this.resultArray = [];
    this.num = 0;
    this.obj;
    this.Loader = function (url) {
        this.url = url;
        EventDispatcher.apply(this);
    }
    this.load = function () {
        for (var i = 0; i < this.url.length; i++) {
            var sendParam = new SendParamNormal();
            sendParam.successHandle = this.loadSuccess;
            sendParam.failHandle = this.loadFail;
            sendParam.object = this;
            sendParam.type = $T.httpConfig.TYPE_GET;
            sendParam.url = this.url[i];
            sendParam.returnType = $T.httpConfig.RETURN_TYPE_HTML;
            sendParam.isStatic = true;
            sendParam.lockKey = this.url[i];
            $T.httpUtilNormal.send(sendParam);
        }

    }
    this.loadSuccess = function (result, sendParam) {
        this.resultArray[sendParam.url] = result;
        this.num++;
        if (this.num == this.url.length) {
            this.dispatchEventWith($T.resourceEventType.LOAD_COMPLETE);
        }
    }
    this.loadFail = function () {

    }
}