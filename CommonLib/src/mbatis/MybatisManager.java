package mbatis;

import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MybatisManager {
	private static SqlSessionFactory sqlSessionFactory;

	public static void init() throws Throwable {
		Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
	}

	public static SqlSession getSqlSession() {
		return sqlSessionFactory.openSession();
	}
}
