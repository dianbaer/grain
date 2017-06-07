function DisplayObject() {
    this.obj;
    this.xValue = 0;
    this.yValue = 0;
    this.alphaValue = 0;
    this.visibility = "visible";
    this.DisplayObject = function (obj) {
        this.obj = obj;
    }
    this.getX = function () {
        return this.xValue;
    }
    this.setX = function (value) {
        this.xValue = value;
        this.draw();
    }
    this.getY = function () {
        return this.yValue;
    }
    this.setY = function (value) {
        this.yValue = value;
        this.draw();
    }
    this.getAlpha = function () {
        return this.alphaValue;
    }
    this.setAlpha = function (value) {
        this.alphaValue = value;
        this.draw();
    }
    this.setVisible = function (value) {
        if (value == true) {
            this.visibility = "visible";
        } else {
            this.visibility = "hidden";
        }
        this.draw();
    }
    this.draw = function () {
        this.obj.style.position = "absolute";
        this.obj.style.top = this.yValue + "px";
        this.obj.style.left = this.xValue + "px";
        this.obj.style.opacity = this.alphaValue;
        this.obj.style.filter = "alpha(opacity=" + (this.alphaValue * 100) + "%)";
        this.obj.style.visibility = this.visibility;
    }
}
