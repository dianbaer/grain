package config;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import log.LogManager;
import net.sf.json.JSONObject;

public class ConfigManager {
	private static Hashtable<Class<ITemplate>, Hashtable<Integer, ITemplate>> templateData;

	private static HashMap<String, JSONObject> jsonData;

	public static enum JsonConfigType {
		CONFIG("config"), CONFIGEXT("configext");

		private String typeValue;

		private JsonConfigType(String typeValue) {
			this.typeValue = typeValue;
		}

		public String getTypeValue() {
			return typeValue;
		}

		public void setTypeValue(String typeValue) {
			this.typeValue = typeValue;
		}

	};

	public static void init(String configDir, String configName) throws Throwable {
		templateData = new Hashtable<Class<ITemplate>, Hashtable<Integer, ITemplate>>();
		jsonData = new HashMap<String, JSONObject>();
		if (configDir == null || configDir.equals("") || configName == null || configName.equals("")) {
			throw new Exception("配置文件信息为空");
		}
		LogManager.initLog.info("加载通用配置文件");

		if (!configDir.endsWith("/") && !configDir.endsWith("\\")) {
			configDir += "/";
		}
		String xmlPath = configDir + configName;
		LogManager.initLog.info("通用配置文件路径为：" + xmlPath);
		XMLConfiguration xmlConfiguration = new XMLConfiguration(xmlPath);
		List<HierarchicalConfiguration> configs = xmlConfiguration.configurationsAt("Configs.Config");

		Class<ITemplate>[] supportDataClasses = new Class[configs.size()];
		String[] dataPath = new String[configs.size()];

		for (int i = 0; i < configs.size(); i++) {
			Configuration config = configs.get(i);
			supportDataClasses[i] = (Class<ITemplate>) Class.forName(config.getString("class"));
			dataPath[i] = config.getString("path");
		}
		for (int i = 0; i < dataPath.length; i++) {
			LogManager.initLog.info("加载文本配置文件：" + configDir + dataPath[i]);
			List<String> templates = ConfigUtil.readTemplate(configDir + dataPath[i]);
			if (templates != null && templates.size() > 0) {
				Hashtable<Integer, ITemplate> templateTable = new Hashtable<Integer, ITemplate>();
				for (int j = 0; j < templates.size(); j++) {
					String[] data = ((String) templates.get(j)).split("\t");
					ITemplate template = ((ITemplate) supportDataClasses[i].newInstance()).initTemplate(data);
					templateTable.put(template.getId(), template);
				}
				templateData.put(supportDataClasses[i], templateTable);
			}
			LogManager.initLog.info("加载文本配置文件：" + configDir + dataPath[i] + "完成");
		}
		List<HierarchicalConfiguration> jsonConfigs = xmlConfiguration.configurationsAt("JsonConfigs.Config");
		for (int i = 0; i < jsonConfigs.size(); i++) {
			Configuration config = jsonConfigs.get(i);
			LogManager.initLog.info("加载json配置文件：" + configDir + config.getString("path"));
			JSONObject json = ConfigUtil.readJson(configDir + config.getString("path"));
			jsonData.put(config.getString("class"), json);
			LogManager.initLog.info("加载json配置文件：" + configDir + config.getString("path") + "完成");
		}
		CommonConfig.init();
		LogManager.initLog.info("加载通用配置文件完成");
	}

	public static JSONObject getJsonData(String jsonConfigType) {
		return jsonData.get(jsonConfigType);
	}

	public static Hashtable<Integer, ITemplate> getTemplateData(Class<? extends ITemplate> clazz) {
		return templateData.get(clazz);
	}

	public static ITemplate getTemplateData(Class<? extends ITemplate> clazz, int id) {
		if (templateData.get(clazz) == null) {
			return null;
		}
		ITemplate template = templateData.get(clazz).get(id);
		return template;
	}
}
