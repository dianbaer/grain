$T.sBubbleChains = new Array();
$T.functionMapping = new Array();
/**
 * xp已看完，这个类重中之重就是回调之后，不能影响这次循环的调用，一切都有一个衡量点，就是如果注册了这个时间，并且触发时，除非是被阻止冒泡，不能阻止事件触发
 * 比较重要的几点，如果事件触发了，接受这个事件的对象无法改变自身这一次的监听状态例如
 * a对象监听函数有b和c，b先收到，在这一次b里面移除c的监听，并不能阻止c收到这次事件
 * 如果想阻止，可以再b里面把stopsImmediatePropagation设置为true
 * 接受这个事件的对象可以移除他的上一级的监听，使之无法收到事件（这种做法不合理，本来就不能做）
 * 如果接受这个事件对象，离开了自己的父类，也不能阻止父类这一次收到这个事件
 */
function EventDispatcher() {
    this.mEventListeners;
    this.isEventDispatcher = true;
    /** xp添加监听,同一个函数只能添加一种类型的监听，多余的忽略，在监听的时候动态的创建，节省资源（无回调） * */
    this.addEventListener = function (type, listener, parent) {
        if (this.mEventListeners == null)
            this.mEventListeners = new Array();

        var listeners = this.mEventListeners[type];
        if (listeners == null) {
            this.mEventListeners[type] = [listener];
            $T.functionMapping[listener] = parent;
        } else if ($T.arrayTools.indexOf(listeners, listener) == -1) { // check
            // for
            // duplicates
            listeners.push(listener);
            $T.functionMapping[listener] = parent;
        }
    }
    /** xp移除监听，这里面临时创建了一个数组，存还需要在监听的函数，这里能创建大量的临时数组，这是有意义的，因为回调的时候可能会删除事件，这个删除事件，不能影响这一次的遍历（无回调）* */
    this.removeEventListener = function (type, listener) {
        if (this.mEventListeners) {
            var listeners = this.mEventListeners[type];
            var numListeners = listeners ? listeners.length : 0;

            if (numListeners > 0) {
                // we must not modify the original vector, but work on a copy.
                // (see comment in 'invokeEvent')

                var index = 0;
                var restListeners = new Array();

                for (var i = 0; i < numListeners; ++i) {
                    var otherListener = listeners[i];
                    if (otherListener != listener)
                        restListeners[index++] = otherListener;
                }

                this.mEventListeners[type] = restListeners;
            }
        }
    }
    /** xp移除这个类型的所有监听，如果不传类型，就移除所有类型的监听（无回调）* */
    this.removeEventListeners = function (type) {
        if (type && this.mEventListeners)
            delete this.mEventListeners[type];
        else
            this.mEventListeners = null;
    }
    /***************************************************************************
     * xp如果不冒泡并且 （事件字典为空或者字典里没有这个事件）就不发事件，这样提高效率
     **************************************************************************/
    this.dispatchEvent = function (event) {
        var bubbles = event.mBubbles;

        if (!bubbles && (this.mEventListeners == null || !(event.mType in this.mEventListeners)))
            return; // no need to do anything

        // we save the current target and restore it later;
        // this allows users to re-dispatch events without creating a clone.

        // xp存这个事件最开始设定的目标，执行完毕之后在还原，一般情况下这个目标肯定是空，除非用户自己设定了目标
        var previousTarget = event.mTarget;
        // xp设置目标
        event.mTarget = this;
        // xp只有设置冒泡，并且这个目标是显示对象，才去走冒泡逻辑，不然不走，提高效率
        if (bubbles && this.isDisplayObject)
            this.bubbleEvent(event);
        else
            this.invokeEvent(event);

        if (previousTarget)
            event.mTarget = previousTarget;
    }
    /** xp 这里的回调，可能增加事件无所谓，因为加的事件在长度之外，也可能删除事件，删除事件已经解决了这个问题，创建新的数组* */
    this.invokeEvent = function (event) {
        var listeners = this.mEventListeners ? this.mEventListeners[event.mType] : null;
        var numListeners = listeners == null ? 0 : listeners.length;

        if (numListeners) {
            // xp这个this，并不是发事件的那个显示对象，谁调的，就是谁
            event.mCurrentTarget = this;

            // we can enumerate directly over the vector, because:
            // when somebody modifies the list while we're looping,
            // "addEventListener" is not
            // problematic, and "removeEventListener" will create a new Vector,
            // anyway.

            for (var i = 0; i < numListeners; ++i) {
                var listener = listeners[i];
                var numArgs = listener.length;

                if (numArgs == 0)
                    listener.call($T.functionMapping[listener]);
                else if (numArgs == 1)
                    listener.call($T.functionMapping[listener], event);
                else
                    listener.call($T.functionMapping[listener], event, event.mData);

                if (event.mStopsImmediatePropagation) {
                    // xp如果这个数组被换掉了，就把这个数组置空把，没用了（回来测试下）
                    if (!this.mEventListeners || listeners !== this.mEventListeners[event.mType]) {
                        listeners.length = 0;
                    }
                    return true;
                }
            }
            // xp如果这个数组被换掉了，就把这个数组置空把，没用了（回来测试下）
            if (!this.mEventListeners || listeners !== this.mEventListeners[event.mType]) {
                listeners.length = 0;
            }
            return event.mStopsPropagation;
        } else {
            return false;
        }
    }
    /** xp把全部监听的对象到头放到一个数组里，防止回调给他们删除，然后执行事件冒泡，这个冒泡的数据组池，提高效率而且必须是数组，因为回调之后再发布事件，还需要创建新的* */
    this.bubbleEvent = function (event) {
        // we determine the bubble chain before starting to invoke the
        // listeners.
        // that way, changes done by the listeners won't affect the bubble
        // chain.

        var chain;
        var element = this;
        var length = 1;

        if ($T.sBubbleChains.length > 0) {
            chain = $T.sBubbleChains.pop();
            chain[0] = element;
        } else
            chain = [element];

        while ((element = element.parent) != null)
            chain[length++] = element;

        for (var i = 0; i < length; ++i) {
            var stopPropagation = chain[i].invokeEvent(event);
            if (stopPropagation)
                break;
        }

        chain.length = 0;
        $T.sBubbleChains.push(chain);
    }
    /** xp发布事件用内部的事件池，执行事件发布之后，然后再放入事件池，如果是冒泡或者自己有这个事件 才去发布事件，这个判断增加效率，* */
    this.dispatchEventWith = function (type, bubbles, data) {
        if (bubbles == undefined) {
            bubbles = false;
        }
        if (data == undefined) {
            data = null;
        }
        if (bubbles || this.hasEventListener(type)) {
            var event = $T.eventPool.fromPool(type, bubbles, data);
            this.dispatchEvent(event);
            $T.eventPool.toPool(event);
        }
    }
    /** xp返回是否有这种类型的监听（无回调）* */
    this.hasEventListener = function (type) {
        var listeners = this.mEventListeners ? this.mEventListeners[type] : null;
        return listeners ? listeners.length != 0 : false;
    }
}
