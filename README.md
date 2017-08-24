# ThreeCSS

[![Build Status](https://travis-ci.org/dianbaer/threecss.svg?branch=master)](https://travis-ci.org/dianbaer/threecss)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/45515be2d3bb427e9ffa6bbb62123b8d)](https://www.codacy.com/app/232365732/threecss?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=dianbaer/threecss&amp;utm_campaign=Badge_Grade)

ThreeCSS可用于开发网站、实时通讯、MMORPG等多种大跨度、有状态或无状态的项目，基于Java的轻量级分布式框架。

https://www.threecss.com


1、多线程模型：

	扩展性强，易于使用，高性能，统一化，可配置化管理，
	与tcp、websocket、线程通讯的消息、业务轮训进行了良好的整合并支持优先级划分的系统内部多线程模型。
	支持tcp、websocket、线程通讯的消息与线程ID进行绑定队列化处理或分配随机线程处理。
	支持初始化注入业务轮训，动态增减业务轮训。支持单次线程轮训按优先级顺序处理。

2、分布式锁：

	去中心化思路、基于tcp与系统内部多线程模型的分布式锁（支持锁类型的单键值）。
	支持锁客户端根据类型匹配多台锁服务器，多对多的关系，不同类型互不影响。
	支持锁服务器按类型的键值进行随机多线程的划分，不同类型互不影响，相同类型不同键值互不影响。

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

2、网盘：https://github.com/dianbaer/threecss-box
	
体验地址：https://box.threecss.com

3、嵌入式聊天：https://github.com/dianbaer/threecss-embed-chat
	
体验地址：https://embedchat.threecss.com

4、支付平台：https://github.com/dianbaer/threecss-pay
	
体验地址：https://pay.threecss.com

5、问答：https://github.com/dianbaer/threecss-question
	
体验地址：https://question.threecss.com

6、MMORPF：https://github.com/dianbaer/threecss-mmorpg
	
体验地址：https://mmorpg.threecss.com


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
	
	
	

