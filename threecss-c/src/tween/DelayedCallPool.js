function DelayedCallPool() {
    this.sDelayedCallPool = new Array();

    this.fromPool = function (call, delay, args) {
        if (args == undefined) {
            args = null;
        }
        if (this.sDelayedCallPool.length)
            return this.sDelayedCallPool.pop().reset(call, delay, args, true);
        else {
            var delayedCall = new DelayedCall();
            delayedCall.DelayedCall(call, delay, args, true);
            return delayedCall;
        }
    }
    this.toPool = function (delayedCall) {
        // reset any object-references, to make sure we don't prevent any
        // garbage collection
        delayedCall.mCall = null;
        delayedCall.mArgs = null;
        // delayedCall.removeEventListeners();
        this.sDelayedCallPool[this.sDelayedCallPool.length] = delayedCall;
    }
}
$T.delayedCallPool = new DelayedCallPool();