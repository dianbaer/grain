function ResourceManager() {
    this.resource = [];
    this.loadResource = function (url, callBack) {
        var loader = new Loader();
        loader.Loader(url);
        loader.obj = callBack;
        loader.addEventListener($T.resourceEventType.LOAD_COMPLETE, this.loadComplete, this);
        loader.load();
    }
    this.loadComplete = function (event) {
        event.mTarget.removeEventListener($T.resourceEventType.LOAD_COMPLETE, this.loadComplete);
        for (var i = 0; i < event.mTarget.url.length; i++) {
            var url = event.mTarget.url[i];
            this.resource[url] = event.mTarget.resultArray[url];
        }
        event.mTarget.obj.call(null, event.mTarget.url);
    }
    this.getResource = function (url) {
        return this.resource[url];
    }
}
$T.resourceManager = new ResourceManager();