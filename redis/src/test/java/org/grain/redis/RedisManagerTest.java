//package org.grain.redis;
//
//import static org.junit.Assert.assertEquals;
//
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//public class RedisManagerTest {
//
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		RedisManager.init("127.0.0.1", 6379, null);
//	}
//
//	@Test
//	public void testSetStringValue() {
//		RedisManager.setStringValue("111", "222");
//		String str = RedisManager.getStringValue("111");
//		assertEquals(true, (str != null && str.equals("222")));
//	}
//
//	@Test
//	public void testSetObjValue() {
//		RedisTest test = new RedisTest("3333", "4444");
//		RedisManager.setObjValue("3333", test);
//		RedisTest test1 = (RedisTest) RedisManager.getObjValue("3333");
//		assertEquals(true, test1 != null);
//	}
//
//}
