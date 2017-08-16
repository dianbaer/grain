# ThreeCSS

ThreeCSS一套轻量级分布式框架。基于一套优秀的线程模型对分布式锁、等待锁、多线程锁有良好的支持，同时规范了HTTP、TCP、WebScoket的通讯格式。

https://www.threecss.com

	ThreeCSS规范并实现了一套客户端与服务器、服务器与服务器、客户端自身、服务器自身的通讯模式，同时也统一了服务器的线程模型。

	CSS代表Client-Server-Server。客户端采用JavaScript，服务器采用Java。

	通过使用ThreeCSS开发者不必考虑任何通信问题，只需按照具体业务设计系统架构、业务架构即可。


ThreeCSS由threecss-c与threecss-ss两个项目组成。

threecss-c核心功能包含：

	事件、动画、MVC与消息、模块、资源、网络(Http,WebSocket)六个模块组成。

threecss-ss核心功能包含：

	资源与配置、日志、服务器统一线程模型、消息(基于服务器统一线程模型)、网络HTTP(基于容器线程--Tomcat8.5)、
	
	网络TCP(基于Mina线程--Mina2.0.16与服务器统一线程模型)、网络WebSocket(基于容器线程--Tomcat8.5与服务器统一线程模型)、
	
	分布式锁及等待锁(基于网络TCP)、多线程锁、持久与缓存(mariadb、mongodb、redis)。
	

基于ThreeCSS开发的项目：

1、身份：https://github.com/dianbaer/threecss-identity

2、网盘：https://github.com/dianbaer/threecss-box
	
体验地址：https://box.threecss.com

3、嵌入式聊天：https://github.com/dianbaer/threecss-embed-chat
	
体验地址：https://embedchat.threecss.com

4、支付平台：https://github.com/dianbaer/threecss-pay
	
体验地址：https://pay.threecss.com

5、问答：https://github.com/dianbaer/threecss-question
	
体验地址：https://question.threecss.com


推荐环境：

	jdk-8u121

	apache-tomcat-8.5.12

	MariaDB-10.1.22

	CentOS-7-1611

	mongodb-3.4.3（可选）

	redis-2.8.19（可选）

	支持Html5浏览器

打版本：

threecss-ss在项目根目录下，配置好build-custom.properties每个项目的路径及发布路径，执行

	ant
	
threecss-c进入threecss-c目录下，执行

	npm install
	
	grunt
	
	
	

