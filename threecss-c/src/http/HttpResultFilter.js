function HttpResultFilter() {
    this.filterArray = new Array();
    this.addFilter = function (filter) {
        this.filterArray.push(filter);
    }
    this.filter = function (result, sendParam) {
        for (var i = 0; i < this.filterArray.length; i++) {
            var filter = this.filterArray[i];
            var bool = filter.filter(result, sendParam);
            if (!bool) {
                return false;
            }
        }
        return true;
    }
}
$T.httpResultFilter = new HttpResultFilter();