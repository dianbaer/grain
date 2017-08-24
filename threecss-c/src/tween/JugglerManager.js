/**
 * 全局 依赖Juggler
 */
function JugglerManager() {
    this.processTime = 0;
    this.oneJuggler = "";
    this.twoJuggler = "";
    this.threeJuggler = "";
    this.fourJuggler = "";
    this.intervalId = "";
    this.isStop = false;
    this.init = function () {
        this.processTime = new Date().getTime();
        this.oneJuggler = new Juggler();
        this.oneJuggler.Juggler();
        this.twoJuggler = new Juggler();
        this.twoJuggler.Juggler();
        this.threeJuggler = new Juggler();
        this.threeJuggler.Juggler();
        this.fourJuggler = new Juggler();
        this.fourJuggler.Juggler();
        this.intervalId = setInterval(this.onEnterFrame, 25);
    }
    this.onEnterFrame = function () {
        var now = new Date().getTime();
        var passedTime = (now - $T.jugglerManager.processTime) / 1000.0;
        $T.jugglerManager.processTime = now;
        if (passedTime == 0.0 || this.isStop) {
            return;
        }
        $T.jugglerManager.oneJuggler.advanceTime(passedTime);
        $T.jugglerManager.twoJuggler.advanceTime(passedTime);
        $T.jugglerManager.threeJuggler.advanceTime(passedTime);
        $T.jugglerManager.fourJuggler.advanceTime(passedTime);
    }
}
$T.jugglerManager = new JugglerManager();
$T.jugglerManager.init();