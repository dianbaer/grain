package org.grain.threadmsg;

import org.grain.msg.MsgManager;
import org.grain.thread.AsyncThreadManager;
import org.junit.BeforeClass;
import org.junit.Test;

public class ThreadMsgManagerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AsyncThreadManager.init(100, 10, 3, 0, null);
		AsyncThreadManager.start();
		MsgManager.init(true, null);
		ThreadMsgManager.addMapping("createuser", new int[] { 1, 1 });
		TestThreadMsgListener testThreadMsgListener = new TestThreadMsgListener();
		MsgManager.addMsgListener(testThreadMsgListener);
	}

	@Test
	public void testAddMapping() throws InterruptedException {
		ThreadMsgManager.dispatchThreadMsg("createuser", 111, 222);
		ThreadMsgManager.dispatchThreadMsg("updateuser", 111, 222);
		Thread.sleep(1000);
	}

}
