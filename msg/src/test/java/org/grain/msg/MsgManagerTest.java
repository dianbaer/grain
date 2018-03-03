package org.grain.msg;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

public class MsgManagerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MsgManager.init(true, null);
	}

	@Test
	public void testAddMsgListener() throws Exception {
		TestMsgListener testMsgListener = new TestMsgListener();
		boolean result = MsgManager.addMsgListener(testMsgListener);

		MsgManager.dispatchMsg("createuser", 111, 222);
		assertEquals(true, result);
	}

}
