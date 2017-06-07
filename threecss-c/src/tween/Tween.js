/**
 * 动画的核心就是长度等于（终点-起点）*（现在时间/总时间），这是最稳定的动画，没有误差
 * tween池里出来的，回归tween池，自己创建的，需要自己注销，tween内部不负责注销
 */
function Tween() {
    this.mTarget;
    this.mTransitionFunc;
    this.mTransitionName;

    this.mProperties;
    this.mStartValues;
    this.mEndValues;

    this.mOnStart;
    this.mOnUpdate;
    this.mOnRepeat;
    this.mOnComplete;

    this.mOnStartArgs;
    this.mOnUpdateArgs;
    this.mOnRepeatArgs;
    this.mOnCompleteArgs;

    this.mTotalTime;
    this.mCurrentTime;
    this.mProgress;
    this.mDelay;
    this.mRoundToInt;
    this.mNextTween;
    this.mRepeatCount;
    this.mRepeatDelay;
    this.mReverse;
    this.mCurrentCycle;
    // xp增加的
    this.isPoolTween = false;
    this.isTween = true;
    this.Tween = function (target, time, transition, isPoolTween) {
        if (transition == undefined) {
            transition = "linear";
        }
        if (isPoolTween == undefined) {
            isPoolTween = false;
        }
        this.reset(target, time, transition, isPoolTween);
    }
    this.reset = function (target, time, transition, isPoolTween) {
        if (transition == undefined) {
            transition = "linear";
        }
        if (isPoolTween == undefined) {
            isPoolTween = false;
        }
        this.isPoolTween = isPoolTween;
        this.mTarget = target;
        this.mCurrentTime = 0.0;
        this.mTotalTime = Math.max(0.0001, time);
        this.mProgress = 0.0;
        this.mDelay = this.mRepeatDelay = 0.0;
        // xp把repeat也置空,nextTween置空
        this.mOnStart = this.mOnUpdate = this.mOnRepeat = this.mOnComplete = null;
        this.mOnStartArgs = this.mOnUpdateArgs = this.mOnRepeatArgs = this.mOnCompleteArgs = null;
        this.mNextTween = null;
        this.mRoundToInt = this.mReverse = false;
        this.mRepeatCount = 1;
        this.mCurrentCycle = -1;
        this.setTransition(transition);

        if (this.mProperties) {
            this.mProperties.length = 0;
        } else {
            this.mProperties = new Array();
        }
        if (this.mStartValues) {
            this.mStartValues.length = 0;
        } else {
            this.mStartValues = new Array();
        }
        if (this.mEndValues) {
            this.mEndValues.length = 0;
        } else {
            this.mEndValues = new Array();
        }
        EventDispatcher.apply(this);
        return this;
    }
    this.animate = function (getValue, setValue, endValue) {
        if (this.mTarget == null)
            return; // tweening null just does nothing.

        this.mProperties[this.mProperties.length] = setValue;
        this.mStartValues[this.mStartValues.length] = getValue.call(this.mTarget);
        this.mEndValues[this.mEndValues.length] = endValue;
    }
    this.advanceTime = function (time) {
        if (time == 0 || (this.mRepeatCount == 1 && this.mCurrentTime == this.mTotalTime))
            return;

        var i;
        var previousTime = this.mCurrentTime;
        var restTime = this.mTotalTime - this.mCurrentTime;
        var carryOverTime = time > restTime ? time - restTime : 0.0;

        this.mCurrentTime += time;

        if (this.mCurrentTime <= 0)
            return; // the delay is not over yet
        else if (this.mCurrentTime > this.mTotalTime)
            this.mCurrentTime = this.mTotalTime;
        // xp上一次时间小于等于0，当前时间大于0 才说明tween开始了mCurrentTime<=0 return
        // 这里mCurrentTime是肯定大于0的所以mCurrentTime > 0可以删除
        if (this.mCurrentCycle < 0 && previousTime <= 0) {
            this.mCurrentCycle++;
            if (this.mOnStart != null)
                this.mOnStart.call(this, this.mOnStartArgs);
        }

        var ratio = this.mCurrentTime / this.mTotalTime;
        var reversed = this.mReverse && (this.mCurrentCycle % 2 == 1);
        var numProperties = this.mStartValues.length;
        this.mProgress = reversed ? this.mTransitionFunc.call($T.transitions, 1.0 - ratio) : this.mTransitionFunc.call($T.transitions, ratio);
        // xp这块写得非常好，都是长度乘以比值，这样就不会有误差
        for (i = 0; i < numProperties; ++i) {
            // if (isNaN(mStartValues[i]))
            // if (this.mStartValues[i] != this.mStartValues[i]) // isNaN check
            // - "isNaN" causes allocation!
            // this.mStartValues[i] = this.mTarget[this.mProperties[i]];

            var startValue = this.mStartValues[i];
            var endValue = this.mEndValues[i];
            var delta = endValue - startValue;

            var currentValue = startValue + this.mProgress * delta;
            if (this.mRoundToInt)
                currentValue = Math.round(currentValue);
            this.mProperties[i].call(this.mTarget, currentValue);
            // this.mTarget[this.mProperties[i]] = currentValue;
        }

        if (this.mOnUpdate != null)
            this.mOnUpdate.call(this, this.mOnUpdateArgs);
        // xp 这块也是，只有可能等于
        if (previousTime < this.mTotalTime && this.mCurrentTime == this.mTotalTime) {
            if (this.mRepeatCount == 0 || this.mRepeatCount > 1) {
                this.mCurrentTime = -this.mRepeatDelay;
                this.mCurrentCycle++;
                if (this.mRepeatCount > 1)
                    this.mRepeatCount--;
                if (this.mOnRepeat != null)
                    this.mOnRepeat.call(this, this.mOnRepeatArgs);
            } else {
                // xp这块写的太牛逼了，保存成功回调函数和回调参数，防止派发事件的时候清理这些属性，很安全
                // save callback & args: they might be changed through an event
                // listener
                var onComplete = this.mOnComplete;
                var onCompleteArgs = this.mOnCompleteArgs;

                // in the 'onComplete' callback, people might want to call
                // "tween.reset" and
                // add it to another juggler; so this event has to be dispatched
                // *before*
                // executing 'onComplete'.
                this.dispatchEventWith($T.tweenEventType.REMOVE_FROM_JUGGLER);
                if (onComplete != null)
                    onComplete.call(this, onCompleteArgs);
            }
        }
        // xp 这块也不用担心，advanceTime开头有限制，如果是完成了，直接返回
        if (carryOverTime)
            this.advanceTime(carryOverTime);
    }
    // this.getEndValue = function(property)
    // {
    // var index = this.mProperties.indexOf(property);
    // if (index == -1) return null;
    // else return this.mEndValues[index];
    // }
    this.isComplete = function () {
        // xp 这块也是，只有可能等于
        return this.mCurrentTime == this.mTotalTime && this.mRepeatCount == 1;
    }
    this.setTransition = function (value) {
        this.mTransitionName = value;
        this.mTransitionFunc = $T.transitions.getTransition(value);

        // if (mTransitionFunc == null)
        // throw new ArgumentError("Invalid transiton: " + value);
    }
    this.setTransitionFunc = function (value) {
        this.mTransitionName = "custom";
        this.mTransitionFunc = value;
    }
    /** xp延迟这块是先加回原先的延迟，再减去现在的延迟，没问题* */
    this.setDelay = function (value) {
        this.mCurrentTime = this.mCurrentTime + this.mDelay - value;
        this.mDelay = value;
    }
    // xp注销tween
    this.dispose = function () {
        this.mOnStart = this.mOnUpdate = this.mOnRepeat = this.mOnComplete = null;
        this.mOnStartArgs = this.mOnUpdateArgs = this.mOnRepeatArgs = this.mOnCompleteArgs = null;
        this.mTarget = null;
        this.mTransitionFunc = null;
        this.mProperties.length = 0;
        // mProperties = null;
        this.mStartValues.length = 0;
        // mStartValues = null;
        this.mEndValues.length = 0;
        // mEndValues = null;
        // 这个tween是外部创建的，应该外部注销，这里只是把引用删除
        this.mNextTween = null;
        // this.removeEventListeners();
    }
}