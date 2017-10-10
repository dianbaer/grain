# grain-mongodb

grain-mongodb mongodb工具类，快速操作mongodb


此项目依赖

	grain-log
	gson-2.8.0.jar
	mongo-java-driver-3.4.2.jar
	mongodb数据库

使用

1、创建数据库

	mongo --host 172.27.108.73 --eval 'db = db.getSiblingDB("test");db.createUser({user: "test",pwd: "test",roles: [ "readWrite", "dbAdmin" ]})'
	

2、链接mongodb某数据库

	MongodbManager.init("172.27.108.73", 27017, "test", "test", "test", null);
	
3、创建表

	boolean result = MongodbManager.createCollection("test_table");
	
4、插入数据

TestMongo---------数据类型都需要继承MongoObj对象，为了方便删除和修改

	package org.grain.mongo;
	public class TestMongo extends MongoObj {
		private String id;
		private String name;
		public TestMongo(String id, String name) {
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

	TestMongo testMongo = new TestMongo(UUID.randomUUID().toString(), "name");
	boolean result = MongodbManager.insertOne("test_table", testMongo);
	
5、插入列表

	TestMongo testMongo = new TestMongo(UUID.randomUUID().toString(), "name");
	TestMongo testMongo1 = new TestMongo(UUID.randomUUID().toString(), "name1");
	List<MongoObj> list = new ArrayList<>();
	list.add(testMongo);
	list.add(testMongo1);
	boolean result = MongodbManager.insertMany("test_table", list);
	
6、查询列表

	Bson filter = Filters.and(Filters.eq("id", "222"));
	List<TestMongo> list = MongodbManager.find("test_table", filter, TestMongo.class, 0, 0);
	
7、删除数据

	boolean result = MongodbManager.deleteById("test_table", testMongo);
	
8、修改数据

	boolean result = MongodbManager.updateById("test_table", testMongo);
	
9、获取个数

	long count = MongodbManager.count("test_table", null);