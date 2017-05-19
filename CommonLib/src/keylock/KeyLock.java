package keylock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import log.LogManager;

public class KeyLock {

	private Map<Object, Long> lockMap = new ConcurrentHashMap<Object, Long>();
	private Lock lock = new ReentrantLock();
	private Condition con = lock.newCondition();

	public void lock(Object key) throws KeyLockException {
		try {
			lock.lock();
			if (key == null) {
				throw new KeyLockException("KeyLock key is null can not lock");
			}
			Long time = lockMap.get(key);
			while (time != null) {

				if (System.currentTimeMillis() - time.longValue() >= KeyLockManager.KEY_LOCK_EXPIRE_TIME) {
					throw new KeyLockException("lock time out");
				}
				// 如有其他的,唤醒其他的.没有等待时间过了自己醒来
				con.await(KeyLockManager.KEY_LOCK_CYCLE_SLEEP_TIME, TimeUnit.MILLISECONDS);

				time = lockMap.get(key);
			}
			lockMap.put(key, new Long(System.currentTimeMillis()));
		} catch (Exception e) {
			LogManager.keylockLog.error("keylock异常", e);
			throw new KeyLockException(e.getMessage());
		} finally {
			con.signal();
			lock.unlock();
		}
	}

	public void unlock(Object key) {
		if (key != null)
			lockMap.remove(key);
	}

	public void lock(Object key1, Object key2) throws KeyLockException {
		if (key1 == null && key2 == null) {
			throw new KeyLockException("KeyLock key1 and key2 is null can not lock");
		}
		if (key1 == null) {
			lock(key2);
			return;
		}
		if (key2 == null) {
			lock(key1);
			return;
		}
		if (key1.equals(key2)) {
			lock(key1);
			return;
		}
		// 防止互锁,永远保证第一个是小的、第二个是大的
		if (key1.hashCode() > key2.hashCode()) {
			Object temp = key1;
			key1 = key2;
			key2 = temp;
		}
		// 第一个抛异常了，无所谓
		lock(key1);
		try {
			// 第二个抛异常了，需要解锁第一个
			lock(key2);
		} catch (Exception e) {
			LogManager.keylockLog.error("keylock异常", e);
			unlock(key1);
			throw new KeyLockException(e.getMessage());
		}
	}

	public void unlock(Object key1, Object key2) {
		if (key1 == null) {
			unlock(key2);
			return;
		}
		if (key2 == null) {
			unlock(key1);
			return;
		}
		if (key1.equals(key2)) {
			unlock(key1);
			return;
		}
		if (key1.hashCode() > key2.hashCode()) {
			Object temp = key1;
			key1 = key2;
			key2 = temp;
		}
		unlock(key1);
		unlock(key2);
	}
}
