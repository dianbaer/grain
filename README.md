# grain

[![Build Status](https://travis-ci.org/dianbaer/grain.svg?branch=master)](https://travis-ci.org/dianbaer/grain)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c6563ece3c3d4fb5b0ec08ce99e537ee)](https://www.codacy.com/app/232365732/grain?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=dianbaer/grain&amp;utm_campaign=Badge_Grade)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)


### grain是一个颗粒化RPC框架，可与任何框架整合。同时包含多对多关系的分布式锁、基于servlet的httpserver、websocket、系统多线程模型、多线程锁等组件，按需选择组件，不绑架开发者。


## grain架构图（深颜色是核心组件强烈推荐，具有不错的特性）


![grain架构图](./grain-framework.png "grain-framework.png")

### 注意：如果一台服务器已经承担分布式锁服务器的角色，就不要用该服务器承担别的角色，因为这台服务器每个线程会等待锁客户端释放锁而阻塞线程。

### 开发者可根据项目情况按需选择组件，例如：

>1、grain-thread（系统多线程模型）。依赖：grain-log

>2、grain-threadmsg（系统多线程模型，线程消息通讯）。依赖：grain-thread，grain-msg

>3、grain-rpc（远程对象访问）。依赖：grain-threadmsg，grain-tcp

>4、grain-distributedlock（多对多关系的分布式锁）。依赖：grain-rpc

>5、grain-threadwebsocket（websocket服务器，使用系统多线程模型处理业务）。依赖：grain-websocket-lib，grain-threadmsg

>6、grain-httpserver（基于servlet的http服务器）。依赖：grain-log

>7、grain-threadkeylock（支持锁类型单键值与双键值的多线程锁）。依赖：grain-log


### github地址：

https://github.com/dianbaer/grain

### 码云地址：

https://gitee.com/dianbaer/grain


## 核心组件介绍


### 1、grain-thread（系统多线程模型）


	grain最核心的组件，支撑起上层很多组件包含threadmsg（线程消息通讯）、
	rpc（远程对象访问）、distributedlock（多对多关系的分布式锁）、threadwebsocket（websocket服务器）。
	
	grain-thread支持创建多线程池，业务轮询精准注入指定线程ID，任意消息（例如：msg、tcp、websocket等）精准注入指定线程ID。


[grain-thread-了解更多](./grain-thread)

---


### 2、grain-threadmsg（系统多线程模型，线程消息通讯）


	系统多线程之间的通讯，业务线程跳转都依赖此组件。

	
[grain-threadmsg-了解更多](./grain-threadmsg)

---


### 3、grain-rpc（远程对象访问）


	通过grain-rpc可以创建RPC客户端与服务器进行远程对象访问。多线程阻塞，唤醒等复杂的多线程业务都已内部解决。
	
	
简单例子：
	
	
	1、创建消息包
	RPCTestC.Builder builder = RPCTestC.newBuilder();
	builder.setName("RPC你好啊");
	TcpPacket pt = new TcpPacket(TestTCode.TEST_RPC_C, builder.build());
	2、远程调用
	TcpPacket ptReturn = WaitLockManager.lock(session, pt);
	

[grain-rpc-了解更多](./grain-rpc)


例子（包含RPC客户端与RPC服务器，直接运行main函数即可）：


[grain-rpc-clienttest](./grain-rpc-clienttest)

[grain-rpc-servertest](./grain-rpc-servertest)


---


### 4、grain-distributedlock（多对多关系的分布式锁）


	去中心化思路，通过grain-distributedlock可以创建分布式锁服务器与锁客户端。
	grain-distributedlock不同类型互不影响，相同类型不同键值互不影响。仅仅当类型与键值都相等时会进行分布式阻塞。
	锁客户端与锁服务器的双向线程阻塞，服务器匹配、类型键值与线程ID的匹配都已内部解决。
	
简单例子：

	// 获取锁
	int lockId = DistributedLockClient.getLock("111", "user");
	if (lockId == 0) {
		return;
	}
	/*********** 执行分布式锁业务逻辑 *********/
	System.out.println("分布式锁id为：" + lockId);
	/*********** 执行分布式锁业务逻辑 *********/
	// 释放锁
	DistributedLockClient.unLock("111", "user", lockId);
	
	
[grain-distributedlock-了解更多](./grain-distributedlock)


例子（包含分布式锁客户端与服务器，直接运行main函数即可）：


[grain-distributedlock-clienttest](./grain-distributedlock-clienttest)


[grain-distributedlock-servertest](./grain-distributedlock-servertest)


---


### 5、grain-threadwebsocket（websocket服务器创建）


	将grain-threadwebsocket包引入web工程，可以创建websocket服务器。
	（业务分发至系统多线程模型grain-thread，可以精准指派某业务归属线程ID）
	
	
[grain-threadwebsocket-了解更多](./grain-threadwebsocket)


例子（该例子内部含有js websocket客户端，使用tomcat启动即可）：


[grain-threadwebsocket-test](./grain-threadwebsocket-test)


---


### 6、grain-httpserver（创建http服务器）

	定义关键字并统筹所有请求参数，进行数据格式化。支持文件与操作数据的隔离。
	支持post表单数据与json数据，支持表单文件，支持get拼接参数，支持扩展消息包过滤器，支持扩展请求回复类型。
	
[grain-httpserver-了解更多](./grain-httpserver)


例子（该例子内部含有js http客户端，使用tomcat启动即可）：


[grain-httpserver-test](./grain-httpserver-test)


---


### 7、grain-threadkeylock（支持锁类型单键值与双键值的多线程锁）


	在多线程业务中，支持锁类型的单键值与双键值，并且支持锁函数
	
	
简单例子1（锁函数）：当类型为TEST1，键值为111同时调用函数时，会进行锁定。
	
	
	public String lockFunction(Object... params) {}
	String str = (String) KeyLockManager.lockMethod("111", TEST1, (params) -> lockFunction(params), new Object[] { "222", 111 });
	
简单例子2（锁函数）：当类型为TEST1，键值为111或222同时调用函数时，会进行锁定。

	
	String str = (String) KeyLockManager.lockMethod("111", "222", TEST1, (params) -> lockFunction(params), new Object[] { "222", 111 });
	

[grain-threadkeylock-了解更多](./grain-threadkeylock)


---


## 其他组件介绍


[grain-log-了解更多](./grain-log)

[grain-msg-了解更多](./grain-msg)
	
[grain-tcp-了解更多](./grain-tcp)

[grain-config-了解更多](./grain-config)

[grain-reds-了解更多](./grain-redis)

[grain-mongodb-了解更多](./grain-mongodb)

[grain-mariadb-了解更多](./grain-mariadb)	
	
[grain-websocket-了解更多](./grain-websocket)

[grain-httpclient-了解更多](./grain-httpclient)


## 打版本

	ant
	
## 依赖

	java8
	
