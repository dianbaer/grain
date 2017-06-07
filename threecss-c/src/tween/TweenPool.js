function TweenPool() {
    this.sTweenPool = new Array();

    this.fromPool = function (target, time, transition) {
        if (transition == undefined) {
            transition = "linear";
        }
        if (this.sTweenPool.length)
            return this.sTweenPool.pop().reset(target, time, transition, true);
        else {
            var tween = new Tween();
            tween.Tween(target, time, transition, true)
            return tween;
        }
    }

    this.toPool = function (tween) {
        // reset any object-references, to make sure we don't prevent any
        // garbage collection
        tween.mOnStart = tween.mOnUpdate = tween.mOnRepeat = tween.mOnComplete = null;
        tween.mOnStartArgs = tween.mOnUpdateArgs = tween.mOnRepeatArgs = tween.mOnCompleteArgs = null;
        tween.mTarget = null;
        tween.mTransitionFunc = null;
        // xp这里增加清除nextTween
        tween.mNextTween = null;
        // tween.removeEventListeners();
        this.sTweenPool[this.sTweenPool.length] = tween;
    }
}
$T.tweenPool = new TweenPool();