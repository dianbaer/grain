package org.grain.thread;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.BeforeClass;
import org.junit.Test;

public class AsyncThreadManagerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AsyncThreadManager.init(100, 10, 3, 0, null);
		AsyncThreadManager.start();
	}

	@Test
	public void testAddHandle() throws NoSuchMethodException, SecurityException, InterruptedException {
		PacketTest packetTest = new PacketTest();
		Method method = HandlerManagerTest.class.getMethod("handle", new Class[] { Object.class });
		ThreadHandle threadHandle = new ThreadHandle(packetTest, method, null);
		boolean result = AsyncThreadManager.addHandle(threadHandle, 1, 1);
		Thread.sleep(1000);
		assertEquals(true, result);
	}

	@Test
	public void testAddCycle() {
		CycleTest cycleTest = new CycleTest();
		cycleTest.name = "testAddCycle";
		boolean result = AsyncThreadManager.addCycle(cycleTest, 1, 1);
		assertEquals(true, result);
	}

	@Test
	public void testRemoveCycle() {
		CycleTest cycleTest = new CycleTest();
		cycleTest.name = "testRemoveCycle";
		boolean result = AsyncThreadManager.addCycle(cycleTest, 1, 1);
		result = AsyncThreadManager.removeCycle(cycleTest, 1, 1);
		assertEquals(true, result);
	}

	@Test
	public void testGetRandomThreadPriority() {
		int[] threadPriority = AsyncThreadManager.getRandomThreadPriority();
		assertEquals(true, threadPriority != null);
	}

	@Test
	public void testGetRandomThread() {
		int[] threadPriority = AsyncThreadManager.getRandomThread();
		assertEquals(true, threadPriority != null);
	}

}
