package org.grain.config;

import static org.junit.Assert.assertEquals;

import java.util.Hashtable;

import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.json.JSONObject;

public class ConfigManagerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ConfigManager.init("./TestConfigData/", "config_map_test.xml", null);
	}

	@Test
	public void testGetJsonData() {
		JSONObject js = ConfigManager.getJsonData("config");
		assertEquals(true, js != null);
	}

	@Test
	public void testGetTemplateData() {
		Hashtable<String, ITemplate> hashTable = ConfigManager.getTemplateData(TestTemplate.class);
		assertEquals(true, hashTable != null);
	}

	@Test
	public void testGetTemplateDataById() {
		TestTemplate hashTable = (TestTemplate)ConfigManager.getTemplateData(TestTemplate.class,"1");
		assertEquals("1", hashTable.getId());
	}

}
