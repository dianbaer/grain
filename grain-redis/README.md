# grain-redis

grain-redis redis工具类，快速操作redis


此项目依赖

	grain-log
	commons-pool2-2.4.2.jar
	jedis-2.9.0.jar
	redis缓存数据库

使用


1、链接redis

	RedisManager.init("127.0.0.1", 6379, null);
	
2、存取字符串数据

	RedisManager.setStringValue("111", "222");
	String str = RedisManager.getStringValue("111");
	
3、存取序列化对象数据


RedisTest------实现Serializable接口的可序列化对象

	package org.grain.redis;
	import java.io.Serializable;
	public class RedisTest implements Serializable {
		private static final long serialVersionUID = 1L;
		private String id;
		private String name;
		public RedisTest(String id, String name) {
			this.id = id;
			this.name = name;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}

存取操作

	RedisTest test = new RedisTest("3333", "4444");
	RedisManager.setObjValue("3333", test);
	RedisTest test1 = (RedisTest) RedisManager.getObjValue("3333");