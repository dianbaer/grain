function Transitions() {
    this.LINEAR = "linear";
    this.EASE_IN = "easeIn";
    this.EASE_OUT = "easeOut";
    this.EASE_IN_OUT = "easeInOut";
    this.EASE_OUT_IN = "easeOutIn";
    this.EASE_IN_BACK = "easeInBack";
    this.EASE_OUT_BACK = "easeOutBack";
    this.EASE_IN_OUT_BACK = "easeInOutBack";
    this.EASE_OUT_IN_BACK = "easeOutInBack";
    this.EASE_IN_ELASTIC = "easeInElastic";
    this.EASE_OUT_ELASTIC = "easeOutElastic";
    this.EASE_IN_OUT_ELASTIC = "easeInOutElastic";
    this.EASE_OUT_IN_ELASTIC = "easeOutInElastic";
    this.EASE_IN_BOUNCE = "easeInBounce";
    this.EASE_OUT_BOUNCE = "easeOutBounce";
    this.EASE_IN_OUT_BOUNCE = "easeInOutBounce";
    this.EASE_OUT_IN_BOUNCE = "easeOutInBounce";

    this.sTransitions = null;
    this.getTransition = function (name) {
        if (this.sTransitions == null)
            this.registerDefaults();
        return this.sTransitions[name];
    }
    this.register = function (name, func) {
        if (this.sTransitions == null)
            this.registerDefaults();
        this.sTransitions[name] = func;
    }
    this.registerDefaults = function () {
        this.sTransitions = new Array();

        this.register(this.LINEAR, this.linear);
        this.register(this.EASE_IN, this.easeIn);
        this.register(this.EASE_OUT, this.easeOut);
        this.register(this.EASE_IN_OUT, this.easeInOut);
        this.register(this.EASE_OUT_IN, this.easeOutIn);
        this.register(this.EASE_IN_BACK, this.easeInBack);
        this.register(this.EASE_OUT_BACK, this.easeOutBack);
        this.register(this.EASE_IN_OUT_BACK, this.easeInOutBack);
        this.register(this.EASE_OUT_IN_BACK, this.easeOutInBack);
        this.register(this.EASE_IN_ELASTIC, this.easeInElastic);
        this.register(this.EASE_OUT_ELASTIC, this.easeOutElastic);
        this.register(this.EASE_IN_OUT_ELASTIC, this.easeInOutElastic);
        this.register(this.EASE_OUT_IN_ELASTIC, this.easeOutInElastic);
        this.register(this.EASE_IN_BOUNCE, this.easeInBounce);
        this.register(this.EASE_OUT_BOUNCE, this.easeOutBounce);
        this.register(this.EASE_IN_OUT_BOUNCE, this.easeInOutBounce);
        this.register(this.EASE_OUT_IN_BOUNCE, this.easeOutInBounce);
    }
    this.linear = function (ratio) {
        return ratio;
    }

    this.easeIn = function (ratio) {
        return ratio * ratio * ratio;
    }

    this.easeOut = function (ratio) {
        var invRatio = ratio - 1.0;
        return invRatio * invRatio * invRatio + 1;
    }

    this.easeInOut = function (ratio) {
        return this.easeCombined(this.easeIn, this.easeOut, ratio);
    }

    this.easeOutIn = function (ratio) {
        return this.easeCombined(this.easeOut, this.easeIn, ratio);
    }

    this.easeInBack = function (ratio) {
        var s = 1.70158;
        return Math.pow(ratio, 2) * ((s + 1.0) * ratio - s);
    }

    this.easeOutBack = function (ratio) {
        var invRatio = ratio - 1.0;
        var s = 1.70158;
        return Math.pow(invRatio, 2) * ((s + 1.0) * invRatio + s) + 1.0;
    }

    this.easeInOutBack = function (ratio) {
        return this.easeCombined(this.easeInBack, this.easeOutBack, ratio);
    }

    this.easeOutInBack = function (ratio) {
        return this.easeCombined(this.easeOutBack, this.easeInBack, ratio);
    }

    this.easeInElastic = function (ratio) {
        if (ratio == 0 || ratio == 1)
            return ratio;
        else {
            var p = 0.3;
            var s = p / 4.0;
            var invRatio = ratio - 1;
            return -1.0 * Math.pow(2.0, 10.0 * invRatio) * Math.sin((invRatio - s) * (2.0 * Math.PI) / p);
        }
    }

    this.easeOutElastic = function (ratio) {
        if (ratio == 0 || ratio == 1)
            return ratio;
        else {
            var p = 0.3;
            var s = p / 4.0;
            return Math.pow(2.0, -10.0 * ratio) * Math.sin((ratio - s) * (2.0 * Math.PI) / p) + 1;
        }
    }

    this.easeInOutElastic = function (ratio) {
        return this.easeCombined(this.easeInElastic, this.easeOutElastic, ratio);
    }

    this.easeOutInElastic = function (ratio) {
        return this.easeCombined(this.easeOutElastic, this.easeInElastic, ratio);
    }

    this.easeInBounce = function (ratio) {
        return 1.0 - this.easeOutBounce(1.0 - ratio);
    }

    this.easeOutBounce = function (ratio) {
        var s = 7.5625;
        var p = 2.75;
        var l;
        if (ratio < (1.0 / p)) {
            l = s * Math.pow(ratio, 2);
        } else {
            if (ratio < (2.0 / p)) {
                ratio -= 1.5 / p;
                l = s * Math.pow(ratio, 2) + 0.75;
            } else {
                if (ratio < 2.5 / p) {
                    ratio -= 2.25 / p;
                    l = s * Math.pow(ratio, 2) + 0.9375;
                } else {
                    ratio -= 2.625 / p;
                    l = s * Math.pow(ratio, 2) + 0.984375;
                }
            }
        }
        return l;
    }

    this.easeInOutBounce = function (ratio) {
        return this.easeCombined(this.easeInBounce, this.easeOutBounce, ratio);
    }

    this.easeOutInBounce = function (ratio) {
        return this.easeCombined(this.easeOutBounce, this.easeInBounce, ratio);
    }

    this.easeCombined = function (startFunc, endFunc, ratio) {
        if (ratio < 0.5)
            return 0.5 * startFunc.call($T.transitions, ratio * 2.0);
        else
            return 0.5 * endFunc.call($T.transitions, (ratio - 0.5) * 2.0) + 0.5;
    }
}
$T.transitions = new Transitions();