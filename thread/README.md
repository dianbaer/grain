# grain-thread

## grain-thread 通用线程管理工具，可以注入业务轮训和消息处理。最核心的组件


此项目依赖

	grain-log

使用

1、初始化异步线程 每次轮训间隔100毫秒，10条异步线程，每天线程3个优先级,锁定线程数0，日志null
启动异步线程

	AsyncThreadManager.init(100, 10, 3, 0, null);
	AsyncThreadManager.start();

2、将要处理的事件放入线程1优先级1的异步线程

PacketTest-------消息包

	package org.grain.thread;
	public class PacketTest {
		public PacketTest() {
		}
	}

HandlerManagerTest-------处理消息包的方法

	package org.grain.thread;
	public class HandlerManagerTest {
		public static void handle(Object packet) {
			System.out.println("HandlerManagerTest.handle，线程：" + Thread.currentThread().getName());
		}
	}

	
分配至异步线程处理
	
	PacketTest packetTest = new PacketTest();
	Method method = HandlerManagerTest.class.getMethod("handle", new Class[] { Object.class });
	ThreadHandle threadHandle = new ThreadHandle(packetTest, method, null);
	boolean result = AsyncThreadManager.addHandle(threadHandle, 1, 1);

3、将要处理的轮训放入线程1优先级1的异步线程

CycleTest----实现ICycle接口的轮训类

	package org.grain.thread;
	public class CycleTest implements ICycle {
		public String name;
		@Override
		public void cycle() throws Exception {
			System.out.println(name + "业务轮训，线程：" + Thread.currentThread().getName());
		}
		@Override
		public void onAdd() throws Exception {
			System.out.println(name + "加入动作，线程：" + Thread.currentThread().getName());
		}
		@Override
		public void onRemove() throws Exception {
			System.out.println(name + "离开动作，线程：" + Thread.currentThread().getName());
		}
	}


	CycleTest cycleTest = new CycleTest();
	cycleTest.name = "testAddCycle";
	boolean result = AsyncThreadManager.addCycle(cycleTest, 1, 1);

4、将轮训移除线程1优先级1的异步线程

	CycleTest cycleTest = new CycleTest();
	cycleTest.name = "testRemoveCycle";
	boolean result = AsyncThreadManager.addCycle(cycleTest, 1, 1);
	result = AsyncThreadManager.removeCycle(cycleTest, 1, 1);

5、获取随机线程随机优先级

	int[] threadPriority = AsyncThreadManager.getRandomThreadPriority();

6、获取随机线程优先级1

	int[] threadPriority = AsyncThreadManager.getRandomThread();
	
7、获取锁定的第几个线程

	AsyncThreadManager.getLockThreadPriority(1)


