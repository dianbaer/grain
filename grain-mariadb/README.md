# grain-mariadb

grain-mariadb mariadb工具类，快速操作mariadb


此项目依赖

	grain-log
	mariadb-java-client-1.5.7.jar
	mybatis-3.4.2.jar
	mybatis-generator-core-1.3.5.jar
	mariadb数据库

使用


1、创建数据库

	src/test/resources/TestConfigData/mariadbtest.sql

2、生成映射文件，修改PaginationPlugin.java与generatorConfig.xml并执行PaginationPlugin.java文件main函数

	src/test/java/org/grain/mariadb/PaginationPlugin.java
	src/test/resources/TestConfigData/generatorConfig.xml
	
生成文件示例

		org.grain.mariadb.dao.base-----接口
		org.grain.mariadb.mappers.base----xml
		org.grain.mariadb.model.base----实体类
	
3、修改mybatis-config.xml文件

	src/test/resources/TestConfigData/mybatis-config.xml
	
4、启动程序

	MybatisManager.init("C:\\Users\\admin\\Desktop\\github\\grain\\trunk\\grain-mariadb\\src\\test\\resources\\TestConfigData\\", "mybatis-config.xml", null);
	
5、操作数据库

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
	

	