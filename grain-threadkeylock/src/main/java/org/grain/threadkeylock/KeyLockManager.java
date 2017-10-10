package org.grain.threadkeylock;

import java.util.HashMap;
import java.util.Map;

import org.grain.log.ILog;

public class KeyLockManager {
	public static int KEY_LOCK_EXPIRE_TIME;
	public static int KEY_LOCK_CYCLE_SLEEP_TIME;
	private static ILog log;
	public static Map<String, KeyLock> keyLockMap = new HashMap<String, KeyLock>();

	/**
	 * 初始化锁类型
	 * 
	 * @param types
	 *            需要初始化锁类型
	 * @param keyLockExpireTime
	 *            锁的过期时间一般是120000 2分钟即可
	 * @param keyLockCycleSleepTime
	 *            锁轮训睡眠时间一般是100 100毫秒即可
	 * @param log
	 *            日志 log 可以为null
	 */
	public static void init(String[] types, int keyLockExpireTime, int keyLockCycleSleepTime, ILog log) {
		KeyLockManager.KEY_LOCK_EXPIRE_TIME = keyLockExpireTime;
		KeyLockManager.KEY_LOCK_CYCLE_SLEEP_TIME = keyLockCycleSleepTime;
		KeyLockManager.log = log;
		for (int i = 0; i < types.length; i++) {
			String type = types[i];
			KeyLock keyLock = new KeyLock(keyLockExpireTime, keyLockCycleSleepTime, log);
			keyLockMap.put(type, keyLock);
		}
	}

	/**
	 * 根据类型获取锁
	 * 
	 * @param type
	 * @return
	 */
	public static KeyLock getKeyLock(String type) {
		return keyLockMap.get(type);
	}

	/**
	 * 锁某类型单键值的例子
	 */
	private static void keyLockTest() {
		boolean isKeyLockException = false;
		KeyLock keyLock = KeyLockManager.getKeyLock("TEST1");
		String lockKey = "11";
		try {
			keyLock.lock(lockKey);
			if (KeyLockManager.log != null) {
				KeyLockManager.log.info("xxxxxx");
			}
		} catch (KeyLockException e) {
			isKeyLockException = true;
			if (KeyLockManager.log != null) {
				KeyLockManager.log.error("keylock自定义异常", e);
			}
		} catch (Exception e) {
			if (KeyLockManager.log != null) {
				KeyLockManager.log.error("业务异常", e);
			}
		} finally {
			// keylock出现的异常，权利释放这个锁。只有添加的这个锁的才有权利释放
			if (!isKeyLockException) {
				keyLock.unlock(lockKey);
			} else {

			}
		}
	}

	/**
	 * 锁某类型双键值的例子
	 */
	private static void keyLockTest1() {
		boolean isKeyLockException = false;
		KeyLock keyLock = KeyLockManager.getKeyLock("TEST1");
		String lockKey1 = "11";
		String lockKey2 = "22";

		try {
			keyLock.lock(lockKey1, lockKey2);
			if (KeyLockManager.log != null) {
				KeyLockManager.log.info("xxxxxx");
			}
		} catch (KeyLockException e) {
			isKeyLockException = true;
			if (KeyLockManager.log != null) {
				KeyLockManager.log.error("keylock自定义异常", e);
			}
		} catch (Exception e) {
			if (KeyLockManager.log != null) {
				KeyLockManager.log.error("业务异常", e);
			}
		} finally {
			// keylock出现的异常，权利释放这个锁。只有添加的这个锁的才有权利释放
			if (!isKeyLockException) {
				keyLock.unlock(lockKey1, lockKey2);
			} else {

			}
		}
	}

	/**
	 * 锁函数针对某类型的单键值
	 * 
	 * @param lockKey
	 *            键值
	 * @param keyType
	 *            类型
	 * @param func
	 *            函数
	 * @param params
	 *            函数需要穿的参数
	 * @return 返回 func的的返回值
	 */
	public static Object lockMethod(String lockKey, String keyType, KeylockFunction func, Object... params) {
		boolean isKeyLockException = false;
		KeyLock keyLock = KeyLockManager.getKeyLock(keyType);
		try {
			keyLock.lock(lockKey);
			return func.apply(params);
		} catch (KeyLockException e) {
			isKeyLockException = true;
			if (KeyLockManager.log != null) {
				KeyLockManager.log.error("keylock自定义异常", e);
			}
		} catch (Exception e) {
			if (KeyLockManager.log != null) {
				KeyLockManager.log.error("业务异常", e);
			}
		} finally {
			// keylock出现的异常，权利释放这个锁。只有添加的这个锁的才有权利释放
			if (!isKeyLockException) {
				keyLock.unlock(lockKey);
			} else {

			}
		}
		return null;
	}

	/**
	 * 锁函数针对某类型的双键值，例如两个人交易
	 * 
	 * @param lockKey1
	 *            键值1
	 * @param lockKey2
	 *            键值2
	 * @param keyType
	 *            类型
	 * @param func
	 *            锁定的函数
	 * @param params
	 *            函数需要传递的参数
	 * @return func的返回值
	 */
	public static Object lockMethod(String lockKey1, String lockKey2, String keyType, KeylockFunction func, Object... params) {
		boolean isKeyLockException = false;
		KeyLock keyLock = KeyLockManager.getKeyLock(keyType);
		try {
			keyLock.lock(lockKey1, lockKey2);
			return func.apply(params);
		} catch (KeyLockException e) {
			isKeyLockException = true;
			if (KeyLockManager.log != null) {
				KeyLockManager.log.error("keylock自定义异常", e);
			}
		} catch (Exception e) {
			if (KeyLockManager.log != null) {
				KeyLockManager.log.error("业务异常", e);
			}
		} finally {
			// keylock出现的异常，权利释放这个锁。只有添加的这个锁的才有权利释放
			if (!isKeyLockException) {
				keyLock.unlock(lockKey1, lockKey2);
			} else {

			}
		}
		return null;
	}
}
