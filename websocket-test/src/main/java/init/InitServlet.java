package init;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.grain.msg.MsgManager;
import org.grain.websokcetlib.WSManager;
import org.slf4j.LoggerFactory;

import protobuf.ws.Test.TestC;
import protobuf.ws.Test.TestS;
import test.GrainLog;
import test.TestWSService;

public class InitServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();

		try {
			GrainLog grainLog = new GrainLog(LoggerFactory.getLogger("minaLog"));
			GrainLog grainLog1 = new GrainLog(LoggerFactory.getLogger("msgLog"));
			// 初始化消息
			MsgManager.init(true, grainLog1);
			WSManager.init(grainLog1);
			// 映射操作码解析类
			WSManager.addMapping("testc", TestC.class);
			WSManager.addMapping("tests", TestS.class);

			// 注册tcp回调函数
			TestWSService testWSService = new TestWSService();
			WSManager.addWSListener(testWSService);

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
