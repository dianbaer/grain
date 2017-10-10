function DemoProxy() {
    this.demoTest = function (userName, userPassword,files) {
        var data = {
            "hOpCode": 1,
            "userName": userName,
            "userPassword": userPassword
        };

        var sendParam = new SendParam();
        sendParam.successHandle = this.demoTestSuccess;
        sendParam.failHandle = this.demoTestFail;
        sendParam.object = this;
        sendParam.data = data;
        sendParam.url = $T.url.url;
        if(files != null && files.length != 0){
        	sendParam.fileArray = files;
        }
        
        $T.httpUtil.send(sendParam);
    }
    this.demoTestSuccess = function (result, sendParam) {
        alert("返回成功" + JSON.stringify(result));
    }
    this.demoTestFail = function (result, sendParam) {
        alert("返回失败")
    }
}
$T.demoProxy = new DemoProxy();