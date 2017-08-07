function SendParamNormal() {
    this.canContinuous = false;// 该函数名是否可以持续发送
    this.lockKey;//锁的对象
    this.loadType;// 加载消息类型
    this.successHandle;// 成功回调
    this.failHandle;// 失败回调
    this.object;// 回调的对象本身
    this.data;// 发送数据
    this.type = $T.httpConfig.TYPE_POST;// 发送类型
    this.async = true;// 是否异步
    this.url;// 发送地址
    this.returnType = $T.httpConfig.RETURN_TYPE_JSON;// 获取类型
    this.isStatic = false;// 静态网页还是动态数据
    this.startTime;// 请求开始时间
    this.endTime;// 请求结束时间
    this.fileArray;// 文件数组
    this.headerKey;//头key数组
    this.headerValue;//头value数组
}