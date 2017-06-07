function ModuleManager() {
    this.moduleUrlToData = [];
    this.moduleTypeToData = [];
    this.loadModule = function (url, view, type, mediator, data) {
        var moduleData = new ModuleData();
        moduleData.view = view;
        moduleData.type = type;
        moduleData.mediator = mediator;
        moduleData.data = data;
        this.moduleUrlToData[url] = moduleData;
        $T.resourceManager.loadResource([url], this.loadComplete);
    }
    this.loadComplete = function (url) {
        var moduleData = $T.moduleManager.moduleUrlToData[url[0]];
        if (moduleData == null) {
            // 还没加载完成就卸载了
            return;
        }
        var resource = $T.resourceManager.getResource(url[0]);
        if (moduleData.type != null) {
            var oldModuleData = $T.moduleManager.moduleTypeToData[moduleData.type];
            if (oldModuleData != null) {
                $T.register.unRegister(oldModuleData.mediator);
                oldModuleData.mediator.dispose();

            }
            $T.moduleManager.moduleTypeToData[moduleData.type] = moduleData;
        }
        moduleData.view.innerHTML = resource;
        moduleData.mediator.init(moduleData.view, moduleData.data);
        $T.register.register(moduleData.mediator);
        $T.viewManager.notifyObservers($T.viewManager.getNotification($T.notification.MODULE_INIT_COMPLETE, url));
    }
    this.unLoadModule = function (url) {
        var moduleData = this.moduleUrlToData[url];
        if (moduleData != null) {
            if (moduleData.mediator.isInit) {
                $T.register.unRegister(moduleData.mediator);
                moduleData.mediator.dispose();
            }
            delete this.moduleUrlToData[url];
            delete this.moduleTypeToData[moduleData.type];
        }
    }
}
$T.moduleManager = new ModuleManager();