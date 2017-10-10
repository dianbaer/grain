package org.grain.log;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

public class LogTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void test() {
		LogObjTest logObjTest = new LogObjTest();
		logObjTest.error(null, null);
		logObjTest.info(null);
		logObjTest.warn(null);
		assertEquals(true, true);
	}

}
