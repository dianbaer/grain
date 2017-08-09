function HttpUtilNormal() {
    this.lockMap = [];// 锁请求用的
    this.send = function (sendParam) {
        if (sendParam.url == null) {
            alert("url不能为空");
            return;
        }
        var form;
        if (sendParam.fileArray != null) {
            try {
                form = new FormData();
            } catch (e) {
                alert("浏览器不支持FormData");
                return;
            }
        }
        if (sendParam.successHandle == null || sendParam.object == null) {
            alert("successHandle或object不能为空");
            return;
        }
        if (sendParam.type == $T.httpConfigNormal.TYPE_POST) {
            if (sendParam.data == null) {
                alert("发送post请求，data不能为空");
                return;
            }
        }

        if (!sendParam.canContinuous && this.lockMap[sendParam.lockKey] == 1) {
            // 发送消息，不能重复请求
            return;
        }
        if (!sendParam.canContinuous && sendParam.lockKey != null) {
            this.lockMap[sendParam.lockKey] = 1;
        }
        var xMLHttpRequest = new XMLHttpRequest();
        if (xMLHttpRequest == null) {
            if (!sendParam.canContinuous && sendParam.lockKey != null) {
                delete this.lockMap[sendParam.lockKey];
            }
            alert("浏览器不支持ajax请求");
            return;
        }
        if (sendParam.loadType != null) {
            // 发送消息，显示某种请求样式
            $T.viewManager.notifyObservers($T.viewManager.getNotification($T.notification.SEND_HTTP_START, sendParam.loadType));
        }
        var url;
        if (sendParam.isStatic) {
            url = $T.version.addVersionToUrl(sendParam.url);
        } else {
            url = $T.version.addVersionAndTimeToUrl(sendParam.url);
        }
        xMLHttpRequest.open(sendParam.type, url, sendParam.async);
        if (sendParam.headerKey != null && sendParam.headerKey.length > 0) {
            for (var i = 0; i < sendParam.headerKey.length; i++) {
                xMLHttpRequest.setRequestHeader(sendParam.headerKey[i], sendParam.headerValue[i]);
            }
        }
        if (sendParam.fileArray != null) {
            for (var i = 0; i < sendParam.fileArray.length; i++) {
                var file = sendParam.fileArray[i];
                form.append("file" + i, file);
            }
            form.append($T.httpConfigNormal.PACKET, encodeURI(JSON.stringify(sendParam.data)));
        }
        this.addHttpListener(xMLHttpRequest, this.sendReturn, sendParam);
        sendParam.startTime = new Date().getTime();
        if (form != null) {
            xMLHttpRequest.send(form);
        } else {
            if (sendParam.type == $T.httpConfigNormal.TYPE_POST) {
				xMLHttpRequest.setRequestHeader('Content-Type', 'application/json');
                xMLHttpRequest.send(JSON.stringify(sendParam.data));
            } else {
                xMLHttpRequest.send();
            }
        }

    }
    this.addHttpListener = function (xMLHttpRequest, sendReturn, sendParam) {
        var onReadyStateHandle = sendReturn;
        if (sendParam != null) {
            onReadyStateHandle = function (event) {
                sendReturn.call(xMLHttpRequest, event, sendParam);
            }
        }
        xMLHttpRequest.onreadystatechange = onReadyStateHandle;

    }
    this.sendReturn = function (event, sendParam) {
        if (this.readyState == 4) {
            sendParam.endTime = new Date().getTime();
            // 删除请求
            if (!sendParam.canContinuous && sendParam.lockKey != null) {
                delete $T.httpUtilNormal.lockMap[sendParam.lockKey];
            }
            if (sendParam.loadType != null) {
                // 发送消息，关闭某种请求样式
                $T.viewManager.notifyObservers($T.viewManager.getNotification($T.notification.SEND_HTTP_END, sendParam.loadType));
            }
            if (this.status == 200) {
                var result;
                if (sendParam.returnType == $T.httpConfigNormal.RETURN_TYPE_JSON) {
                    try {
                        result = JSON.parse(this.responseText);
                    } catch (e) {
                        // 发送错误消息
                        $T.viewManager.notifyObservers($T.viewManager.getNotification($T.notification.SYSTEM_ERROR, "json eval error"));
                        // 回调错误函数
                        if (sendParam.failHandle != null) {
                            sendParam.failHandle.call(sendParam.object, null, sendParam);
                        }
                        alert("json解析异常");
                        return;
                    }
                } else if (sendParam.returnType == $T.httpConfigNormal.RETURN_TYPE_HTML) {
                    result = this.responseText;
                }
                var bool = $T.httpResultFilter.filter(result, sendParam);
                if (bool) {
                    // 回调正确函数
                    sendParam.successHandle.call(sendParam.object, result, sendParam);
                } else {
                    // 回调错误函数
                    if (sendParam.failHandle != null) {
                        sendParam.failHandle.call(sendParam.object, result, sendParam);
                    }
                }
            } else {
                // 发送错误消息
                $T.viewManager.notifyObservers($T.viewManager.getNotification($T.notification.SYSTEM_ERROR, "http return status" + this.status));
                // 回调错误函数
                if (sendParam.failHandle != null) {
                    sendParam.failHandle.call(sendParam.object, null, sendParam);
                }
            }

        } else if (this.readyState == 2) {

        } else if (this.readyState == 3) {

        } else {

        }

    }

}
$T.httpUtilNormal = new HttpUtilNormal();