function HttpUtil() {
    this.send = function (sendParam) {
        if (sendParam.data == null) {
            alert("发送post请求，data不能为空");
            return;
        }
        if (sendParam.data[$T.httpConfig.HOPCODE] == null) {
            alert("发送post请求，data[$T.httpConfig.HOPCODE]不能为空");
            return;
        }
        sendParam.lockKey = sendParam.data[$T.httpConfig.HOPCODE];
        sendParam.headerKey = [];
        sendParam.headerValue = [];
        sendParam.headerKey.push($T.httpConfig.HOPCODE);
        sendParam.headerValue.push(sendParam.data[$T.httpConfig.HOPCODE]);
        if (sendParam.token != null) {
            sendParam.headerKey.push($T.httpConfig.TOKEN);
            sendParam.headerValue.push(sendParam.token);
        }
        if (sendParam.fileArray != null) {
            if (sendParam.fileUuid != null) {
                sendParam.headerKey.push($T.httpConfig.FILE_UUID);
                sendParam.headerValue.push(sendParam.fileUuid);
            }
            sendParam.headerKey.push($T.httpConfig.PACKET);
            sendParam.headerValue.push(encodeURI(JSON.stringify(sendParam.data)));

            sendParam.headerKey.push($T.httpConfig.SEND_TYPE);
            if (sendParam.sendType != null) {
                sendParam.headerValue.push(sendParam.sendType);
            } else {
                sendParam.headerValue.push($T.httpConfig.SEND_TYPE_FILE_SAVE_SESSION);
            }
        }
        $T.httpUtilNormal.send(sendParam);
    }

    this.getRequestUrl = function (sendParam) {
        var packet = encodeURI(JSON.stringify(sendParam.data));
        return sendParam.url + "?" + $T.httpConfig.HOPCODE + "=" + sendParam.data[$T.httpConfig.HOPCODE] + "&token=" + sendParam.token + "&sendType=" + sendParam.sendType + "&receiveType=" + sendParam.receiveType + "&packet=" + packet;
    }

}
$T.httpUtil = new HttpUtil();