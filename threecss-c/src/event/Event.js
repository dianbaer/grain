function Event() {
    this.mTarget;
    this.mCurrentTarget;
    this.mType;
    this.mBubbles;
    this.mStopsPropagation;
    this.mStopsImmediatePropagation;
    this.mData;
    /** xp 创建事件类，传入类型，是否冒泡，和数据（无回调）* */
    this.Event = function (type, bubbles, data) {
        if (bubbles == undefined) {
            bubbles = false;
        }
        if (data == undefined) {
            data = null;
        }
        this.reset(type, bubbles, data);
    }
    /** xp 停止向上冒泡，这轮的事件广播不停（无回调）* */
    this.stopPropagation = function () {
        this.mStopsPropagation = true;
    }
    /** xp 立即停止事件广播（无回调）* */
    this.stopImmediatePropagation = function () {
        this.mStopsPropagation = this.mStopsImmediatePropagation = true;
    }
    // xp清理事件类
    this.dispose = function () {
        this.mData = this.mTarget = this.mCurrentTarget = null;
    }
    this.reset = function (type, bubbles, data) {
        if (bubbles == undefined) {
            bubbles = false;
        }
        if (data == undefined) {
            data = null;
        }
        this.mType = type;
        this.mBubbles = bubbles;
        this.mData = data;
        this.mTarget = this.mCurrentTarget = null;
        this.mStopsPropagation = this.mStopsImmediatePropagation = false;
        return this;
    }
}
