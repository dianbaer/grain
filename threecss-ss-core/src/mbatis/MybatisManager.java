package mbatis;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MybatisManager {
	private static SqlSessionFactory sqlSessionFactory;

	public static void init(String configDir) throws Throwable {
		if (configDir == null || configDir.equals("")) {
			throw new Exception("配置文件信息为空");
		}
		if (!configDir.endsWith("/") && !configDir.endsWith("\\")) {
			configDir += "/";
		}
		String xmlPath = configDir + "mybatis-config.xml";
		InputStream inputStream = new FileInputStream(xmlPath);
		SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
		sqlSessionFactory = builder.build(inputStream);
	}

	public static SqlSession getSqlSession() {
		return sqlSessionFactory.openSession();
	}
}
