function Register() {
    this.register = function (mediator) {
        if (mediator.init == null) {
            alert("mediator没有init方法");
            return false;
        }
        if (mediator.dispose == null) {
            alert("mediator没有dispose方法");
            return false;
        }
        if (mediator.handleNotification == null) {
            alert("mediator没有handleNotification方法");
            return false;
        }
        if (mediator.listNotificationInterests == null) {
            alert("mediator没有listNotificationInterests数组");
            return false;
        }
        $T.viewManager.registerObserverArray(mediator.listNotificationInterests, mediator);
        if (mediator.advanceTime != undefined) {
            $T.jugglerManager.fourJuggler.add(mediator);
        }
        mediator.isInit = true;
    }
    this.unRegister = function (mediator) {
        $T.viewManager.removeObserverArray(mediator.listNotificationInterests, mediator);
        if (mediator.advanceTime != undefined) {
            $T.jugglerManager.fourJuggler.remove(mediator);
        }
        mediator.isInit = false;
    }
}
$T.register = new Register();