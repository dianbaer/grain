# grain-config

grain-config 配置文件加载管理工具，可以加载并管理模板类文件与json类文件。


此项目依赖

	grain-log
	commons-beanutils-1.9.3.jar
	commons-collections-3.2.2.jar
	commons-configuration-1.10.jar
	commons-lang-2.6.jar
	commons-logging-1.2.jar
	ezmorph-1.0.6.jar
	json-lib-2.4-jdk15.jar

使用

1、初始化配置文件，读取路径为./TestConfigData/，名字为config_map_test.xml的配置文件

	ConfigManager.init("./TestConfigData/", "config_map_test.xml", null);

config_map_test.xml----配置文件格式

	<?xml version="1.0" encoding="UTF-8" ?>
	<root>
		<!--模板类型的配置文件-->
		<Configs>
			<Config>
				<!--文件名-->
				<path>test.txt</path>
				<!--映射类名-->
				<class>org.grain.config.TestTemplate</class>
			</Config>
		</Configs>
		<!--json类型的配置文件-->
		<JsonConfigs>
			<Config>
				<!--文件名-->
				<path>config.json</path>
				<!--通过字符串获取-->
				<class>config</class>
			</Config>
		</JsonConfigs>
	</root>
	
test.txt----模板类型的格式，使用tab键隔开，前两行略过

	id	name
	string	string
	1	dianbaer
	2	电霸儿
	
TestTemplate.java----与test.txt相对应的映射类

	package org.grain.config;
	public class TestTemplate implements ITemplate {
		private String id;
		public String name;
		@Override
		public String getId() {
			return id;
		}
		@Override
		public ITemplate initTemplate(String[] data) throws Exception {
			TestTemplate testTemplate = new TestTemplate();
			testTemplate.id = data[0];
			testTemplate.name = data[1];
			return testTemplate;
		}
	}
config.json-----json格式的配置文件

	{
	"test":[true,"test"]
	}

2、获取json格式配置文件

	JSONObject js = ConfigManager.getJsonData("config");
	
3、获取模板类型映射表

	Hashtable<String, ITemplate> hashTable = ConfigManager.getTemplateData(TestTemplate.class);
	
4、获取模板类型映射表中某个id的值

	TestTemplate hashTable = (TestTemplate)ConfigManager.getTemplateData(TestTemplate.class,"1");
