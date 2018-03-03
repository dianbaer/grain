# grain-threadkeylock

grain-threadkeylock 通用的多线程锁，可以锁类型的单键值或双键值，并且支持锁函数


此项目依赖

	grain-log

使用

1、初始化线程key锁管理器 所有类型，锁过期时间2分钟，锁轮训唤醒时间100毫秒，不打日志

	public static String TEST1 = "TEST1";
	public static String TEST2 = "TEST2";

	public static String[] getkeyLockType() {
		return new String[] { TEST1, TEST2 };
	}
	KeyLockManager.init(getkeyLockType(), 120000, 100, null);
	
2、单锁函数：锁类型是TEST1，键值字符串111，锁定函数lockFunction，锁定函数需要传递的参数 String "222" int 111
返回锁定函数赋值到obj

	public String lockFunction(Object... params) {
		String str = (String) params[0];
		int num = (int) params[1];
		System.out.println(str);
		System.out.println(num);
		return str + num;
	}
	Object obj = KeyLockManager.lockMethod("111", TEST1, (params) -> lockFunction(params), new Object[] { "222", 111 });
	
3、双锁函数：锁类型是TEST1，键值字符串111与222，锁定函数lockFunction，锁定函数需要传递的参数 String "222" int 111
返回锁定函数赋值到obj

	public String lockFunction(Object... params) {
		String str = (String) params[0];
		int num = (int) params[1];
		System.out.println(str);
		System.out.println(num);
		return str + num;
	}
	Object obj = KeyLockManager.lockMethod("111", "222", TEST1, (params) -> lockFunction(params), new Object[] { "222", 111 });
	
4、单锁锁函数段

	boolean isKeyLockException = false;
	KeyLock keyLock = KeyLockManager.getKeyLock("TEST1");
	String lockKey = "11";
	try {
		keyLock.lock(lockKey);
		/************执行业务逻辑开始*************/
		/************执行业务逻辑结束*************/
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
	
5、双锁锁函数段

	boolean isKeyLockException = false;
	KeyLock keyLock = KeyLockManager.getKeyLock("TEST1");
	String lockKey1 = "11";
	String lockKey2 = "22";
	try {
		keyLock.lock(lockKey1, lockKey2);
		/************执行业务逻辑开始*************/
		/************执行业务逻辑结束*************/
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


