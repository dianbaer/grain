package org.grain.mariadb;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.UUID;

import org.apache.ibatis.session.SqlSession;
import org.grain.mariadb.dao.base.TesttableMapper;
import org.grain.mariadb.model.base.Testtable;
import org.junit.BeforeClass;
import org.junit.Test;

public class MybatisManagerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MybatisManager.init("C:\\Users\\admin\\Desktop\\github\\grain\\trunk\\grain-mariadb\\src\\test\\resources\\TestConfigData\\", "mybatis-config.xml", null);
	}

	@Test
	public void test() {
		Testtable testtable = new Testtable();
		testtable.setId(UUID.randomUUID().toString());
		testtable.setName("test");
		testtable.setTime(new Date());
		SqlSession sqlSession = null;
		int result = 0;
		try {
			sqlSession = MybatisManager.getSqlSession();
			TesttableMapper testtableMapper = sqlSession.getMapper(TesttableMapper.class);
			result = testtableMapper.insert(testtable);
			if (result == 0) {
				throw new Exception();
			}
			sqlSession.commit();
		} catch (Exception e) {
			if (sqlSession != null) {
				sqlSession.rollback();
			}
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		assertEquals(1, result);
	}

}
