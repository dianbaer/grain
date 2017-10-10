package org.grain.config;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.grain.log.ILog;

import net.sf.json.JSONObject;

public class ConfigManager {
	private static Hashtable<Class<ITemplate>, Hashtable<String, ITemplate>> templateData;

	private static HashMap<String, JSONObject> jsonData;

	private static ILog log;
	private static String configDir;
	private static String configName;

	/**
	 * 初始化读取配置文件
	 * 
	 * @param configDir
	 *            配置路径
	 * @param configName
	 *            配置文件名
	 * @param log
	 *            日志可以为null
	 * @throws Throwable
	 */
	public static void init(String configDir, String configName, ILog log) throws Exception {
		ConfigManager.log = log;
		ConfigManager.configName = configName;
		templateData = new Hashtable<Class<ITemplate>, Hashtable<String, ITemplate>>();
		jsonData = new HashMap<String, JSONObject>();
		if (configDir == null || configDir.equals("") || configName == null || configName.equals("")) {
			throw new Exception("配置文件信息为空");
		}
		if (log != null) {
			log.info("加载通用配置文件");
		}

		if (!configDir.endsWith("/") && !configDir.endsWith("\\")) {
			configDir += "/";
		}
		String xmlPath = configDir + configName;
		if (log != null) {
			log.info("通用配置文件路径为：" + xmlPath);
		}
		XMLConfiguration xmlConfiguration = new XMLConfiguration(xmlPath);
		String path = xmlConfiguration.getBasePath();
		ConfigManager.configDir = path.substring(path.lastIndexOf("///") + 3, path.lastIndexOf("/") + 1);
		List<HierarchicalConfiguration> configs = xmlConfiguration.configurationsAt("Configs.Config");

		Class<ITemplate>[] supportDataClasses = new Class[configs.size()];
		String[] dataPath = new String[configs.size()];

		for (int i = 0; i < configs.size(); i++) {
			Configuration config = configs.get(i);
			supportDataClasses[i] = (Class<ITemplate>) Class.forName(config.getString("class"));
			dataPath[i] = config.getString("path");
		}
		for (int i = 0; i < dataPath.length; i++) {
			if (log != null) {
				log.info("加载文本配置文件：" + ConfigManager.configDir + dataPath[i]);
			}
			List<String> templates = ConfigUtil.readTemplate(ConfigManager.configDir + dataPath[i], log);
			if (templates != null && templates.size() > 0) {
				Hashtable<String, ITemplate> templateTable = new Hashtable<String, ITemplate>();
				for (int j = 0; j < templates.size(); j++) {
					String[] data = ((String) templates.get(j)).split("\t");
					ITemplate template = ((ITemplate) supportDataClasses[i].newInstance()).initTemplate(data);
					templateTable.put(template.getId(), template);
				}
				templateData.put(supportDataClasses[i], templateTable);
			}
			if (log != null) {
				log.info("加载文本配置文件：" + ConfigManager.configDir + dataPath[i] + "完成");
			}
		}
		List<HierarchicalConfiguration> jsonConfigs = xmlConfiguration.configurationsAt("JsonConfigs.Config");
		for (int i = 0; i < jsonConfigs.size(); i++) {
			Configuration config = jsonConfigs.get(i);
			if (log != null) {
				log.info("加载json配置文件：" + ConfigManager.configDir + config.getString("path"));
			}
			JSONObject json = ConfigUtil.readJson(ConfigManager.configDir + config.getString("path"), log);
			jsonData.put(config.getString("class"), json);
			if (log != null) {
				log.info("加载json配置文件：" + ConfigManager.configDir + config.getString("path") + "完成");
			}
		}
		if (log != null) {
			log.info("加载通用配置文件完成");
		}
	}

	/**
	 * 获取json数据
	 * 
	 * @param jsonConfigType
	 *            文件名
	 * @return
	 */
	public static JSONObject getJsonData(String jsonConfigType) {
		return jsonData.get(jsonConfigType);
	}

	/**
	 * 获取模板数据
	 * 
	 * @param clazz
	 *            对应的反射类名
	 * @return
	 */
	public static Hashtable<String, ITemplate> getTemplateData(Class<? extends ITemplate> clazz) {
		return templateData.get(clazz);
	}

	/**
	 * 根据id获取某条模板数据
	 * 
	 * @param clazz
	 *            对应的反射类名
	 * @param id
	 *            对应的id
	 * @return
	 */
	public static ITemplate getTemplateData(Class<? extends ITemplate> clazz, String id) {
		if (templateData.get(clazz) == null) {
			return null;
		}
		ITemplate template = templateData.get(clazz).get(id);
		return template;
	}
}
