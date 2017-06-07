/**
 * 主要注意的是，这个对象并不会被注销，需要保留这个对象的引用，然后注销了，在删除引用
 */
function DelayedCall() {
    this.mCurrentTime;
    this.mTotalTime;
    this.mCall;
    this.mArgs;
    this.mRepeatCount;
    // xp增加的
    this.isPoolDelayedCall = false;
    this.isDelayedCall = true;
    this.DelayedCall = function (call, delay, args, isPoolDelayedCall) {
        if (args == undefined) {
            args = null;
        }
        if (isPoolDelayedCall == undefined) {
            isPoolDelayedCall = false;
        }
        this.reset(call, delay, args, isPoolDelayedCall);
    }
    /***************************************************************************
     * xp默认是一次，如果不用默认值，需要new对象然后修改次数，如果是0次，就说明无限回调，0.0001的限制比这个小的话就用0.00001不能等于0
     * 如果传0，那就等于下一帧调用
     **************************************************************************/
    this.reset = function (call, delay, args, isPoolDelayedCall) {
        if (args == undefined) {
            args = null;
        }
        if (isPoolDelayedCall == undefined) {
            isPoolDelayedCall = false;
        }
        this.isPoolDelayedCall = isPoolDelayedCall;
        this.mCurrentTime = 0;
        this.mTotalTime = Math.max(delay, 0.0001);
        this.mCall = call;
        this.mArgs = args;
        this.mRepeatCount = 1;
        EventDispatcher.apply(this);
        return this;
    }
    this.advanceTime = function (time) {
        var previousTime = this.mCurrentTime;
        this.mCurrentTime = Math.min(this.mTotalTime, this.mCurrentTime + time);

        // xp上一次时间小于总时间，这个是为了确实有过时间差，第二个条件，如果正确肯定是等于，不可能大于
        // 不可能有大于的情况，所以把大于去了
        // 上一次时间必须小于总时间，并且这一次时间必须大于等于总时间，这个是动画的必须条件，概念上这才叫有时间的动画
        if (previousTime < this.mTotalTime && this.mCurrentTime == this.mTotalTime) {

            if (this.mRepeatCount == 0 || this.mRepeatCount > 1) {

                if (this.mRepeatCount > 0)
                    this.mRepeatCount -= 1;
                this.mCurrentTime = 0;
                // xp回调放下面点比较好吧，这样如果修改repeatCount的时候，不至于还被这里置回去
                if (this.mArgs == null) {
                    this.mCall.apply(this);
                } else {
                    this.mCall.apply(this, this.mArgs);
                }

                // xp精确一点时间都不浪费
                this.advanceTime((previousTime + time) - this.mTotalTime);
            } else {
                // save call & args: they might be changed through an event
                // listener
                // xp保存回调的函数和参数
                var call = this.mCall;
                var args = this.mArgs;
                // in the callback, people might want to call "reset" and re-add
                // it to the
                // juggler; so this event has to be dispatched *before*
                // executing 'call'.
                // 先发事件，再回调，不然juggler可能无法清除这个delayedcall
                this.dispatchEventWith($T.tweenEventType.REMOVE_FROM_JUGGLER);

                call.apply(this, args);
            }
        }
    }
    // xp注销，传进来的，不能这里修改需要在外面清空
    this.dispose = function () {
        this.mCall = null;
        this.mArgs = null;
        // removeEventListeners();
    }
    /** xp没问题，因为如果是1的话，就不会再减1了，并且mCurrentTime也不会再改变了* */
    this.isComplete = function () {
        // 不可能有大于的情况，所以把大于去了
        return this.mRepeatCount == 1 && this.mCurrentTime == this.mTotalTime;
    }
}