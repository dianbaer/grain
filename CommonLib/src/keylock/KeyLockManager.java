package keylock;

import java.util.HashMap;
import java.util.Map;

import log.LogManager;

public class KeyLockManager {
	public static int KEY_LOCK_EXPIRE_TIME;
	public static int KEY_LOCK_CYCLE_SLEEP_TIME;
	public static Map<String, KeyLock> keyLockMap = new HashMap<String, KeyLock>();

	public static void init(String keyLockTypeClass, int keyLockExpireTime, int keyLockCycleSleepTime) throws Exception {
		KEY_LOCK_EXPIRE_TIME = keyLockExpireTime;
		KEY_LOCK_CYCLE_SLEEP_TIME = keyLockCycleSleepTime;
		IKeyLockType keyLockType = (IKeyLockType) Class.forName(keyLockTypeClass).newInstance();
		String[] types = keyLockType.getkeyLockType();
		for (int i = 0; i < types.length; i++) {
			String type = types[i];
			KeyLock keyLock = new KeyLock();
			keyLockMap.put(type, keyLock);
		}
	}

	public static KeyLock getKeyLock(String type) {
		return keyLockMap.get(type);
	}

	public static void keyLockTest() {
		boolean isKeyLockException = false;
		KeyLock keyLock = KeyLockManager.getKeyLock(TestKeyLockType.TEST1);
		String lockKey = "11";
		try {
			keyLock.lock(lockKey);
			LogManager.keylockLog.info("xxxxxx");
		} catch (KeyLockException e) {
			isKeyLockException = true;
			LogManager.keylockLog.error("keylock自定义异常", e);
		} catch (Exception e) {
			LogManager.keylockLog.error("业务异常", e);
		} finally {
			// keylock出现的异常，权利释放这个锁。只有添加的这个锁的才有权利释放
			if (!isKeyLockException) {
				keyLock.unlock(lockKey);
			} else {

			}
		}
	}

	public static void keyLockTest1() {
		boolean isKeyLockException = false;
		KeyLock keyLock = KeyLockManager.getKeyLock(TestKeyLockType.TEST1);
		String lockKey1 = "11";
		String lockKey2 = "22";

		try {
			keyLock.lock(lockKey1, lockKey2);

			LogManager.keylockLog.info("xxxxxx");
		} catch (KeyLockException e) {
			isKeyLockException = true;
			LogManager.keylockLog.error("keylock自定义异常", e);
		} catch (Exception e) {
			LogManager.keylockLog.error("业务异常", e);
		} finally {
			// keylock出现的异常，权利释放这个锁。只有添加的这个锁的才有权利释放
			if (!isKeyLockException) {
				keyLock.unlock(lockKey1, lockKey2);
			} else {

			}
		}
	}

	public static Object lockMethod(String lockKey, String keyType, KeylockFunction func, Object... params) {
		boolean isKeyLockException = false;
		KeyLock keyLock = KeyLockManager.getKeyLock(keyType);
		try {
			keyLock.lock(lockKey);
			return func.apply(params);
		} catch (KeyLockException e) {
			isKeyLockException = true;
			LogManager.keylockLog.error("keylock自定义异常", e);
		} catch (Exception e) {
			LogManager.keylockLog.error("业务异常", e);
		} finally {
			// keylock出现的异常，权利释放这个锁。只有添加的这个锁的才有权利释放
			if (!isKeyLockException) {
				keyLock.unlock(lockKey);
			} else {

			}
		}
		return null;
	}

	public static Object lockMethod(String lockKey1, String lockKey2, String keyType, KeylockFunction func, Object... params) {
		boolean isKeyLockException = false;
		KeyLock keyLock = KeyLockManager.getKeyLock(keyType);
		try {
			keyLock.lock(lockKey1, lockKey2);
			return func.apply(params);
		} catch (KeyLockException e) {
			isKeyLockException = true;
			LogManager.keylockLog.error("keylock自定义异常", e);
		} catch (Exception e) {
			LogManager.keylockLog.error("业务异常", e);
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
