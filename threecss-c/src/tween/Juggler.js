/**
 * 类
 */
function Juggler() {
    this.mObjects;
    this.mElapsedTime;
    this.Juggler = function () {
        this.mElapsedTime = 0;
        this.mObjects = new Array();
    }
    /***************************************************************************
     * xp放入的这个对象必须是不在这个数组里，如果在了，就不再继续增加了，防止多次添加的错误
     * 如果这个对象是事件对象，监听一个REMOVE_FROM_JUGGLER的事件（无回调）
     **************************************************************************/
    this.add = function (object) {
        if (object && $T.arrayTools.indexOf(this.mObjects, object) == -1) {
            this.mObjects[this.mObjects.length] = object;

            // var dispatcher:EventDispatcher = object as EventDispatcher;
            if (object.isEventDispatcher)
                object.addEventListener($T.tweenEventType.REMOVE_FROM_JUGGLER, this.onRemove, this);
        }
    }
    /** xp判断是否这个对象（无回调）* */
    this.contains = function (object) {
        return $T.arrayTools.indexOf(this.mObjects, object) != -1;
    }
    /***************************************************************************
     * xp移除一个对象，如果这个对象是事件对象就移除REMOVE_FROM_JUGGLER事件监听，然后把这个数组的这个对象位置置空,
     * 置空是有意义的，因为在调用动画时，可能会动态的增加动画，置空了，可以控制新加入的动画，在下一帧在做操作（无回调）
     * 如果是tween池里出来的，并且是主动调用，而不是事件调用，就把他回放到tween池
     **************************************************************************/
    this.remove = function (object, event) {
        if (object == null)
            return;

        // var dispatcher:EventDispatcher = object as EventDispatcher;
        if (object.isEventDispatcher)
            object.removeEventListener($T.tweenEventType.REMOVE_FROM_JUGGLER, this.onRemove);

        // var tween:Tween = object as Tween;
        if (object.isTween && event == null && object.isPoolTween)
            this.onPooledTweenComplete(object);
        // var delayedCall:DelayedCall = object as DelayedCall;
        if (object.isDelayedCall && event == null && object.isPoolDelayedCall)
            this.onPooledDelayedCallComplete(object);

        var index = $T.arrayTools.indexOf(this.mObjects, object);
        if (index != -1)
            this.mObjects[index] = null;
    }
    /***************************************************************************
     * xp移除这个对象的所有tween，tween肯定是事件类，所以移除REMOVE_FROM_JUGGLER事件（无回调）
     * 这个方法总觉得有问题，移除的时候，如果tween不是pool出来的，必须在外面手动施放，一定得注意
     **************************************************************************/
    this.removeTweens = function (target) {
        if (target == null)
            return;

        for (var i = this.mObjects.length - 1; i >= 0; --i) {
            var tween = this.mObjects[i];
            if (tween.isTween && tween.mTarget == target) {
                tween.removeEventListener($T.tweenEventType.REMOVE_FROM_JUGGLER, this.onRemove);
                // xp这个很安全的，如果是移除事件监听，也不会移除当前这次事件的监听
                if (tween.isPoolTween)
                    this.onPooledTweenComplete(tween);
                this.mObjects[i] = null;
            }
        }
    }
    this.containsTweens = function (target) {
        if (target == null)
            return false;

        for (var i = this.mObjects.length - 1; i >= 0; --i) {
            var tween = this.mObjects[i];
            if (tween.isTween && tween.mTarget == target)
                return true;
        }

        return false;
    }
    this.delayCall = function (call, delay, args) {
        if (call == undefined)
            return null;

        var delayedCall = $T.delayedCallPool.fromPool(call, delay, args);
        // delayedCall.addEventListener(Event.REMOVE_FROM_JUGGLER,
        // onPooledDelayedCallComplete);
        this.add(delayedCall);

        return delayedCall;
    }
    this.repeatCall = function (call, interval, repeatCount, args) {
        if (call == undefined)
            return null;
        if (repeatCount == undefined)
            repeatCount = 0;
        var delayedCall = $T.delayedCallPool.fromPool(call, interval, args);
        delayedCall.mRepeatCount = repeatCount;
        // delayedCall.addEventListener(Event.REMOVE_FROM_JUGGLER,
        // onPooledDelayedCallComplete);
        this.add(delayedCall);

        return delayedCall;
    }
    this.onPooledDelayedCallComplete = function (delayedCall) {
        $T.delayedCallPool.toPool(delayedCall);
    }
    /***************************************************************************
     * xp使用tween对象池，创建tween，优先把所有属性先判断tween有没有，如果有算tween的，如果没有判断tween驱动的对象有没有，有则设置驱动对象往这个值改变，
     * 然后监听REMOVE_FROM_JUGGLER用来回收tween，最后再加入动画列表（无回调）
     **************************************************************************/

    this.tween = function (target, time, properties) {
        var tween = $T.tweenPool.fromPool(target, time);

        for (var property in properties) {
            var value = properties[property];

            if (tween.hasOwnProperty(property))
                tween[property] = value;
            else if (target.hasOwnProperty(property))
                tween.animate(property, value);
            // else
            // throw new ArgumentError("Invalid property: " + property);
        }

        // tween.addEventListener(Event.REMOVE_FROM_JUGGLER,
        // onPooledTweenComplete);
        this.add(tween);
        return tween;
    }
    this.onPooledTweenComplete = function (tween) {
        $T.tweenPool.toPool(tween);
    }
    /***************************************************************************
     * xp
     * 回调的时候添加没问题，在下一帧执行，移除的话也处理好了，只是置空，保证后续添加的在下一帧执行，如果在回调的时候，移除了这个列表里面的一个，如果他在
     * 还没有执行的那段，就不会在执行了，这会不会有问题（？）
     **************************************************************************/
    this.advanceTime = function (time) {
        var numObjects = this.mObjects.length;
        var currentIndex = 0;
        var i;

        this.mElapsedTime += time;
        if (numObjects == 0)
            return;

        // there is a high probability that the "advanceTime" function modifies
        // the list
        // of animatables. we must not process new objects right now (they will
        // be processed
        // in the next frame), and we need to clean up any empty slots in the
        // list.

        // xp把固定数量的动画，放到数组的前端，并执行，是空的全部放入后端
        for (i = 0; i < numObjects; ++i) {
            var object = this.mObjects[i];
            if (object) {
                // shift objects into empty slots along the way
                if (currentIndex != i) {
                    this.mObjects[currentIndex] = object;
                    this.mObjects[i] = null;
                }

                object.advanceTime(time);
                ++currentIndex;
            }
        }

        if (currentIndex != i) {
            numObjects = this.mObjects.length; // count might have changed!

            while (i < numObjects)
                this.mObjects[currentIndex++] = this.mObjects[i++];

            this.mObjects.length = currentIndex;
        }
    }
    /** xp移除，如果是tween，并且这个tween完成了，执行下一个tween，如果发布REMOVE_FROM_JUGGLER事件，就会调用这个方法，并且执行下一个tween* */
    this.onRemove = function (event) {
        this.remove(event.mTarget, event);

        if (event.mTarget.isTween && event.mTarget.isComplete)
            this.add(event.mTarget.nextTween);
        // xp等tween的nextTween放入，在回收tween
        if (event.mTarget.isTween && event.mTarget.isPoolTween)
            this.onPooledTweenComplete(event.mTarget);
        if (event.mTarget.isDelayedCall && event.mTarget.isPoolDelayedCall)
            this.onPooledDelayedCallComplete(event.mTarget);

    }
}