# ThreeCSS

[![Build Status](https://travis-ci.org/dianbaer/threecss.svg?branch=master)](https://travis-ci.org/dianbaer/threecss)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/45515be2d3bb427e9ffa6bbb62123b8d)](https://www.codacy.com/app/232365732/threecss?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=dianbaer/threecss&amp;utm_campaign=Badge_Grade)

[中文](./README.md)

ThreeCSS可用于开发网站、实时通讯、MMORPG等多种大跨度、有状态或无状态的项目，基于Java的轻量级分布式框架。


ThreeCSS分布式框架的核心功能与特点：

1、多线程模型：

	扩展性强，易于使用，高性能，统一化，可配置化管理，
	与tcp、websocket、线程通讯的消息、业务轮训进行了良好的整合并支持优先级划分的系统内部多线程模型。
	支持tcp、websocket、线程通讯的消息与线程ID进行绑定队列化处理或分配随机线程处理。
	支持初始化注入业务轮训，动态增减业务轮训。支持单次线程轮训按优先级顺序处理。

2、分布式锁：

	去中心化思路、基于tcp与系统内部多线程模型的分布式锁（支持锁类型的单键值）。
	支持锁客户端根据类型匹配多台锁服务器，多对多的关系，不同类型互不影响。
	支持锁服务器按类型的键值进行随机多线程的划分，不同类型互不影响，相同类型不同键值互不影响。
	
![锁客户端](https://github.com/dianbaer/threecss/blob/master/example/DistributedLockDemo/%E9%94%81%E5%AE%A2%E6%88%B7%E7%AB%AF.bmp "锁客户端.bmp")
![锁服务器](https://github.com/dianbaer/threecss/blob/master/example/DistributedLockDemo/%E9%94%81%E6%9C%8D%E5%8A%A1%E5%99%A8.bmp "锁服务器.bmp")

3、通讯配置化：

	所有类型的通讯http、tcp、websocket、线程通讯的消息使用Protobuf统一配置化管理，更易缕清系统脉路。

4、等待锁（RPC）：

	基于tcp与系统内部多线程模型的等待锁，远程RPC。
	
5、系统角色化：

	系统角色化概念，通过配置系统既可担任一个角色，也可担任多个角色。
	支持tcp客户端（断线重连，同时链接多台tcp服务器）、tcp服务器、分布式锁客户端、分布式锁服务器、http服务器、http网关服务器等角色。

6、多线程锁：

	高效的多线程锁，支持锁类型的单键值或双键值，支持快捷锁整个函数段。
	
7、日志细化：

	合理的日志划分，不同类型不同级别产生独立的文件，更易追踪系统问题。
	对所有类型通讯http、tcp、websocket、线程通讯的消息，进行详细的日志追踪。
	
8、HTTP：

	http多方位预处理（文件处理、参数处理），支持过滤器扩展。
	良好的支持SSL、跨域等。良好的支持json、Protobuf、url等多方式调用。
	支持多种回复方式json、Protobuf、string、文件、图片、二进制流，并且推送二进制流支持速率可控。
	
9、持久与缓存：

	支持流行的关系型数据库MariaDB，非关系型数据库MongoDB、缓存服务Redis。

10、团队性：

	致力于规范、优化团队开发流程，提高代码编写一致性，更丰富的配置化管理，项目更加易控、易追溯、易复查。

11、广泛性：

	多适应层面，可用于开发网站、实时通讯、MMORPG等多种大跨度、有状态或无状态的项目。

12、多态性：

	既可设计成中心化思路架构，使用tcp（等待锁RPC或非等待）、http，也可设计成去中心化思路架构，使用tcp（分布式锁）。
	既可设计单服务器架构使用多线程锁，也可设计集群架构使用分布式锁

基于ThreeCSS开发的项目：

1、身份：https://github.com/dianbaer/threecss-identity

2、文件存储：https://github.com/dianbaer/threecss-box	

3、嵌入式聊天：https://github.com/dianbaer/threecss-embed-chat	

4、支付平台：https://github.com/dianbaer/threecss-pay

5、问答：https://github.com/dianbaer/threecss-question

6、MMORPG：https://github.com/dianbaer/threecss-mmorpg
	



在开发Http项目时，ThreeCSS更推荐C/S架构的模式。所以专门开发了Client端的JavaScript库供网页前端使用。只需引入：

	<script src="js/lib/threecss-c.js" type="text/javascript"></script>


1、事件Event

	支持自定义事件，事件冒泡。解决包含关系、树形结构关系的解耦合问题。
	通过EventDispatcher.apply(this)语句，即可成为一个事件类，内部派发事件，外部关注事件。

2、动画Tween

	包含时间管理器Juggler、延迟回调DelaydCall、动画Tween。
	时间间隔回调的统一管理，更加可控、可暂停。
	绝对精准化延迟回调DelaydCall和动画Tween解决了setInterval与setTimeout多次调用不精准的问题。
	多条时间线匹配，更好的解决了根据层次关系先后调用的问题。
	动画Tween的多种过渡类型算法。
	
	简单的例子：
	
		var display = new DisplayObject();
		display.DisplayObject(document.getElementById("test"));
		display.setAlpha(1);
		var tween = new Tween();
		tween.Tween(display, 1, $T.transitions.EASE_OUT_BACK);
		tween.animate(display.getX, display.setX, 900);
		tween.animate(display.getY, display.setY, 500);
		$T.jugglerManager.oneJuggler.add(tween);

3、MV模式与消息

	数据层Proxy与视图控制层Mediator通过消息的解耦合，Proxy与Mediator多对多的关系。
	Proxy可以通过消息通知多个Mediator改变自己控制的视图，Mediator也可调用多个Proxy获取数据。
	Mediator与Mediator之间也是通过消息方式进行沟通。
	
4、HTTP

	友好的ajax请求格式，支持POST请求携带多文件。
	支持相同类型请求可选择是否锁定，支持请求生命周期变化的消息派发。
	支持请求结果校验增加过滤器。
	
	简单的例子：
	
		var data = {
			"userName": userName,
			"userPassword": userPassword,
			'userEmail': userEmail
		};
		var sendParam = new SendParamNormal();
		sendParam.successHandle = this.demoTestSuccess;//成功回调
		sendParam.failHandle = this.demoTestFail;//失败回调（可选）
		sendParam.object = this;
		sendParam.data = data;//携带数据
		sendParam.url = $T.url.createExample;//地址
		if (userImg != null && userImg.length != 0) {
			sendParam.fileArray = userImg;//携带文件（可选）
		}
		$T.httpUtilNormal.send(sendParam);

5、资源与模块

	为了解决html界面拆分问题，引入了资源与模块的概念。
	资源支持缓存、多资源加载回调，模块支持加载模块，卸载模块。
	
	简单的例子：
	
		$T.moduleManager.loadModule("html/top.html", document.getElementById("index_top"), 'top', $T.topMediator,data);
	
	解释：
		加载top.html这个模块，它归属ID为index_top的容器，类型为top相同类型会被自动卸载，它的控制器是$T.topMediator，
		模块加载成功后，自动将top.html加入到显示列表，携带data注入$T.topMediator.init方法，并且自动将$T.topMediator关注的消息进行注册
	
6、WebScoket

	WebSocket解决网页端长连接通讯的问题，可以开发实时通讯，游戏等项目。
	ThreeCSS对Webscoket进行了封装，外部通过关注事件即可知道链接成功、链接失败、消息推送过来这些状态。
	
	简单的例子：
	
		1、创建链接，关注事件
		
		this.webSocketClient = new WebSocketClient();
        this.webSocketClient.WebSocketClient($T.url.chat);
        this.webSocketClient.addEventListener($T.webSocketEventType.CONNECTED, this.onConnected, this);
        this.webSocketClient.addEventListener($T.webSocketEventType.CLOSE, this.onClose, this);
		this.webSocketClient.addEventListener($T.webSocketEventType.getMessage("2"), this.onLoginChatServer, this);
		
		2、向服务器推送消息
		
		var data = {
            "wsOpCode": 5,
            "chatContent": chatContent,
            "toType": toType,
            "toTypeId": toTypeId
        };
        this.webSocketClient.send(data);

7、对象池

	ThreeCSS大量使用了对象池的方式，大幅度降低了JavaScript虚拟机垃圾回收的频率，提高性能。
	

推荐环境：

	jdk-8u121

	apache-tomcat-8.5.12

	MariaDB-10.1.22

	CentOS-7-1611

	mongodb-3.4.3（可选）

	redis-2.8.19（可选）

	支持Html5浏览器

打版本：

threecss-ss在项目根目录下，执行

	ant
	
threecss-c进入threecss-c目录下，执行

	npm install
	
	grunt
	
	
	

