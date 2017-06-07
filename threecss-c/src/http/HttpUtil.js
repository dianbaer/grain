function HttpUtil() {
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
        if (sendParam.type == $T.httpConfig.TYPE_POST) {
            if (sendParam.data == null) {
                alert("发送post请求，data不能为空");
                return;
            }
            if (sendParam.data[$T.httpConfig.HOPCODE] == null) {
                alert("发送post请求，data[$T.httpConfig.HOPCODE]不能为空");
                return;
            }
        }
        if (sendParam.returnType == $T.httpConfig.RETURN_TYPE_JSON) {
            if (!sendParam.canContinuous && this.lockMap[sendParam.data[$T.httpConfig.HOPCODE]] == 1) {
                // 发送消息，不能重复请求
                return;
            }
            this.lockMap[sendParam.data[$T.httpConfig.HOPCODE]] = 1;
        } else if (sendParam.returnType == $T.httpConfig.RETURN_TYPE_HTML) {
            if (!sendParam.canContinuous && this.lockMap[sendParam.url] == 1) {
                // 发送消息，不能重复请求
                return;
            }
            this.lockMap[sendParam.url] = 1;
        } else {
            alert("不支持此返回类型" + sendParam.returnType);
            return;
        }
        var xMLHttpRequest = new XMLHttpRequest();
        if (xMLHttpRequest == null) {
            if (sendParam.returnType == $T.httpConfig.RETURN_TYPE_JSON) {
                delete $T.httpUtil.lockMap[sendParam.data[$T.httpConfig.HOPCODE]];
            } else if (sendParam.returnType == $T.httpConfig.RETURN_TYPE_HTML) {
                delete $T.httpUtil.lockMap[sendParam.url];
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
        if (sendParam.returnType == $T.httpConfig.RETURN_TYPE_JSON) {
            xMLHttpRequest.setRequestHeader($T.httpConfig.HOPCODE, sendParam.data[$T.httpConfig.HOPCODE]);
        }
        if (sendParam.fileArray != null) {
            if (sendParam.fileUuid != null) {
                xMLHttpRequest.setRequestHeader($T.httpConfig.FILE_UUID, sendParam.fileUuid);
            }
            xMLHttpRequest.setRequestHeader($T.httpConfig.PACKET, encodeURI(JSON.stringify(sendParam.data)));
            if (sendParam.sendType != null) {
                xMLHttpRequest.setRequestHeader($T.httpConfig.SEND_TYPE, sendParam.sendType);
            } else {
                xMLHttpRequest.setRequestHeader($T.httpConfig.SEND_TYPE, $T.httpConfig.SEND_TYPE_FILE_SAVE_SESSION);
            }
            for (var i = 0; i < sendParam.fileArray.length; i++) {
                var file = sendParam.fileArray[i];
                form.append("file" + i, file);
            }
            form.append($T.httpConfig.PACKET, encodeURI(JSON.stringify(sendParam.data)));
        }
        if (sendParam.token != null) {
            xMLHttpRequest.setRequestHeader($T.httpConfig.TOKEN, sendParam.token);
        }
        this.addHttpListener(xMLHttpRequest, this.sendReturn, sendParam);
        sendParam.startTime = new Date().getTime();
        if (form != null) {
            xMLHttpRequest.send(form);
        } else {
            if (sendParam.data != null && sendParam.type == $T.httpConfig.TYPE_POST) {
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
            if (sendParam.returnType == $T.httpConfig.RETURN_TYPE_JSON) {
                // console.log("操作码为：" + sendParam.data[$T.httpConfig.HOPCODE] +
                // "，请求时间为：" + (sendParam.endTime - sendParam.startTime) +
                // "毫秒");
                delete $T.httpUtil.lockMap[sendParam.data[$T.httpConfig.HOPCODE]];
            } else if (sendParam.returnType == $T.httpConfig.RETURN_TYPE_HTML) {
                // console.log("获取地址为：" + sendParam.url + "，请求时间为：" +
                // (sendParam.endTime - sendParam.startTime) + "毫秒");
                delete $T.httpUtil.lockMap[sendParam.url];
            }
            if (sendParam.loadType != null) {
                // 发送消息，关闭某种请求样式
                $T.viewManager.notifyObservers($T.viewManager.getNotification($T.notification.SEND_HTTP_END, sendParam.loadType));
            }
            if (this.status == 200) {
                var result;
                if (sendParam.returnType == $T.httpConfig.RETURN_TYPE_JSON) {
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
                } else if (sendParam.returnType == $T.httpConfig.RETURN_TYPE_HTML) {
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
            this;
        } else if (this.readyState == 3) {
            this;
        } else {
            this;
        }

    }
    this.getRequestUrl = function (sendParam) {
        var packet = encodeURI(JSON.stringify(sendParam.data));
        return sendParam.url + "?" + $T.httpConfig.HOPCODE + "=" + sendParam.data[$T.httpConfig.HOPCODE] + "&token=" + sendParam.token + "&sendType=" + sendParam.sendType + "&receiveType=" + sendParam.receiveType + "&packet=" + packet;
    }

}
$T.httpUtil = new HttpUtil();