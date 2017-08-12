# ThreeCSS

ThreeCSS一套轻量级分布式框架。http://www.threecss.com

	ThreeCSS规范并实现了一套客户端与服务器、服务器与服务器、客户端自身、服务器自身的通讯模式，同时也统一了服务器的线程模型。

	CSS代表Client-Server-Server。客户端采用JavaScript，服务器采用Java。

	通过使用ThreeCSS开发者不必考虑任何通信问题，只需按照具体业务设计系统架构、业务架构即可。


ThreeCSS由threecss-c与threecss-ss两个项目组成。


threecss-c核心功能包含：

	事件、动画、MVC与消息、模块、资源、网络(Http,WebSocket)六个模块组成。

threecss-ss核心功能包含：

	资源与配置、日志、服务器统一线程模型、消息(服务器统一线程模型)、网络HTTP(基于容器线程--Tomcat8.5)、
	
	网络TCP(基于Mina线程--Mina2.0.16与服务器统一线程模型)、网络WebSocket(基于容器线程--Tomcat8.5与服务器统一线程模型)、
	
	分布式锁及等待锁(基于网络TCP)、多线程锁、持久与缓存(mariadb、mongodb、redis)。
	

基于ThreeCSS开发的项目：


1、身份：https://github.com/dianbaer/threecss-identity


2、网盘：https://github.com/dianbaer/threecss-box
	
体验地址：http://box.threecss.com


3、嵌入式聊天：https://github.com/dianbaer/threecss-embed-chat
	
体验地址：http://embedchat.threecss.com


4、支付平台：https://github.com/dianbaer/threecss-pay
	
体验地址：http://pay.threecss.com


5、问答：https://github.com/dianbaer/threecss-question
	
体验地址：http://question.threecss.com
	
	
	

