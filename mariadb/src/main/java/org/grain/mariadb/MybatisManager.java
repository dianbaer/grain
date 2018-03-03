package org.grain.mariadb;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.grain.log.ILog;

public class MybatisManager {
	private static SqlSessionFactory sqlSessionFactory;
	public static ILog log;

	/**
	 * 初始化链接mariadb
	 * 
	 * @param configDir
	 *            地址
	 * @param configName
	 *            配置文件名
	 * @param log
	 *            日志可为null
	 * @throws Exception
	 */
	public static void init(String configDir, String configName, ILog log) throws Exception {
		if (configDir == null || configDir.equals("")) {
			throw new Exception("配置文件信息为空");
		}
		MybatisManager.log = log;
		if (!configDir.endsWith("/") && !configDir.endsWith("\\")) {
			configDir += "/";
		}
		String xmlPath = configDir + configName;
		InputStream inputStream = new FileInputStream(xmlPath);
		SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
		sqlSessionFactory = builder.build(inputStream);
	}

	/**
	 * 获取session
	 * 
	 * @return
	 */
	public static SqlSession getSqlSession() {
		return sqlSessionFactory.openSession();
	}
}
