package org.grain.log;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class RunMonitorTest {
	private static RunMonitor runMonitor;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		runMonitor = new RunMonitor("TCP", "connect");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		String str = runMonitor.toString();
		System.out.println(str);
		str = runMonitor.toString("disconnect");
		System.out.println(str);
		assertEquals(true, str != null);
	}

	@Test
	public void testPutMonitor() {
		runMonitor.putMonitor("链接");
		runMonitor.putMonitor("发送");
		runMonitor.putMonitor("断开");
	}

}
