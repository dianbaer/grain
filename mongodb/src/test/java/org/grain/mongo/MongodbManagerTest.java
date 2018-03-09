//package org.grain.mongo;
//
//import static org.junit.Assert.assertEquals;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//import org.bson.conversions.Bson;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import com.mongodb.client.model.Filters;
//
//public class MongodbManagerTest {
//
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		MongodbManager.init("172.27.108.73", 27017, "test", "test", "test", null);
//		boolean result = MongodbManager.createCollection("test_table");
//		if (!result) {
//			System.out.println("创建test_table失败");
//		}
//		TestMongo testMongo = new TestMongo("111", "name");
//		result = MongodbManager.insertOne("test_table", testMongo);
//		if (!result) {
//			System.out.println("插入TestMongo失败");
//		}
//	}
//
//	@Test
//	public void testCreateCollection() {
//		boolean result = MongodbManager.createCollection("test_table1");
//		assertEquals(true, result);
//	}
//
//	@Test
//	public void testInsertOne() {
//		TestMongo testMongo = new TestMongo(UUID.randomUUID().toString(), "name");
//		boolean result = MongodbManager.insertOne("test_table", testMongo);
//		assertEquals(true, result);
//	}
//
//	@Test
//	public void testInsertMany() {
//		TestMongo testMongo = new TestMongo(UUID.randomUUID().toString(), "name");
//		TestMongo testMongo1 = new TestMongo(UUID.randomUUID().toString(), "name1");
//		List<MongoObj> list = new ArrayList<>();
//		list.add(testMongo);
//		list.add(testMongo1);
//		boolean result = MongodbManager.insertMany("test_table", list);
//		assertEquals(true, result);
//	}
//
//	@Test
//	public void testFind() {
//		List<TestMongo> list = MongodbManager.find("test_table", null, TestMongo.class, 0, 0);
//		assertEquals(true, list.size() > 0);
//	}
//
//	@Test
//	public void testDeleteById() {
//		TestMongo testMongo = new TestMongo("222", "name");
//		boolean result = MongodbManager.insertOne("test_table", testMongo);
//		Bson filter = Filters.and(Filters.eq("id", "222"));
//		List<TestMongo> list = MongodbManager.find("test_table", filter, TestMongo.class, 0, 0);
//		testMongo = list.get(0);
//		result = MongodbManager.deleteById("test_table", testMongo);
//		assertEquals(true, result);
//	}
//
//	@Test
//	public void testUpdateById() {
//		TestMongo testMongo = new TestMongo("333", "name");
//		boolean result = MongodbManager.insertOne("test_table", testMongo);
//		Bson filter = Filters.and(Filters.eq("id", "333"));
//		List<TestMongo> list = MongodbManager.find("test_table", filter, TestMongo.class, 0, 0);
//		testMongo = list.get(0);
//		testMongo.setName("name" + UUID.randomUUID().toString());
//		result = MongodbManager.updateById("test_table", testMongo);
//		assertEquals(true, result);
//	}
//
//	@Test
//	public void testCount() {
//		long count = MongodbManager.count("test_table", null);
//		assertEquals(true, count > 0);
//	}
//
//}
