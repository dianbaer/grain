package action;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import dao.dao.base.DemoMapper;
import dao.model.base.Demo;
import dao.model.base.DemoCriteria;
import log.LogManager;
import mbatis.MybatisManager;

public class DemoAction {
	public static Demo getDemoByUserName(String userName) {
		if (userName == null || userName.equals("")) {
			return null;
		}
		SqlSession sqlSession = null;
		try {
			sqlSession = MybatisManager.getSqlSession();
			DemoMapper demoMapper = sqlSession.getMapper(DemoMapper.class);
			DemoCriteria demoCriteria = new DemoCriteria();
			DemoCriteria.Criteria criteria = demoCriteria.createCriteria();
			criteria.andUserNameEqualTo(userName);
			List<Demo> demoList = demoMapper.selectByExample(demoCriteria);
			if (demoList == null || demoList.size() == 0) {
				LogManager.mariadbLog.warn("通过userName:" + userName + "获取demo为空");
				return null;
			}
			return demoList.get(0);
		} catch (Exception e) {
			LogManager.mariadbLog.error("获取demo异常", e);
			return null;
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
	}
}
