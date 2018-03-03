package org.grain.threadkeylock;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

public class KeyLockManagerTest {

	public static String TEST1 = "TEST1";
	public static String TEST2 = "TEST2";

	public static String[] getkeyLockType() {
		return new String[] { TEST1, TEST2 };
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		KeyLockManager.init(getkeyLockType(), 120000, 100, null);
	}

	@Test
	public void testGetKeyLock() {
		KeyLock keylock = KeyLockManager.getKeyLock(TEST1);
		assertEquals(true, keylock != null);
	}

	@Test
	public void testLockMethodOneKey() {
		String str = (String) KeyLockManager.lockMethod("111", TEST1, (params) -> lockFunction(params), new Object[] { "222", 111 });
		assertEquals("222111", str);
	}

	public String lockFunction(Object... params) {
		String str = (String) params[0];
		int num = (int) params[1];
		System.out.println(str);
		System.out.println(num);
		return str + num;
	}

	@Test
	public void testLockMethodTwoKey() {
		String str = (String) KeyLockManager.lockMethod("111", "222", TEST1, (params) -> lockFunction(params), new Object[] { "222", 111 });
		assertEquals("222111", str);
	}

	@Test
	public void testLockPartOneKey() {
		boolean isKeyLockException = false;
		KeyLock keyLock = KeyLockManager.getKeyLock(TEST1);
		String lockKey = "111";
		boolean result = false;
		try {
			keyLock.lock(lockKey);
			result = true;
		} catch (KeyLockException e) {
			isKeyLockException = true;
		} catch (Exception e) {
		} finally {
			// keylock出现的异常，权利释放这个锁。只有添加的这个锁的才有权利释放
			if (!isKeyLockException) {
				keyLock.unlock(lockKey);
			} else {

			}
		}
		assertEquals(true, result);
	}

	@Test
	public void testLockPartTwoKey() {
		boolean isKeyLockException = false;
		KeyLock keyLock = KeyLockManager.getKeyLock(TEST1);
		String lockKey1 = "11";
		String lockKey2 = "22";
		boolean result = false;
		try {
			keyLock.lock(lockKey1, lockKey2);
			result = true;
		} catch (KeyLockException e) {
			isKeyLockException = true;
		} catch (Exception e) {
		} finally {
			// keylock出现的异常，权利释放这个锁。只有添加的这个锁的才有权利释放
			if (!isKeyLockException) {
				keyLock.unlock(lockKey1, lockKey2);
			} else {

			}
		}
		assertEquals(true, result);
	}

}
