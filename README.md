# threecss
ThreeCSS是一套轻量级分布式框架，它规范并实现了一套客户端与服务器、服务器与服务器、客户端自身内部、服务器自身内部的通讯模式。CSS代表Client-Server-Server。
ThreeCSS由ThreeCSS-c(javascript)与ThreeCSS-ss(java)两个项目组成。
ThreeCSS-c核心功能包含：事件、动画、MVC与消息、模块、资源、网络(http,websocket)六个模块组成。
ThreeCSS-ss核心功能包含：资源与配置、日志、系统线程、消息(基于系统线程)、网络HTTP(基于容器线程)、网络TCP(基于系统线程)、网络websocket(基于容器线程与系统线程)、
分布式锁及等待锁(基于网络TCP)、多线程锁、持久与缓存(mariadb、mongodb、redis)。
