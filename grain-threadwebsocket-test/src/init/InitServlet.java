package init;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.grain.msg.MsgManager;
import org.grain.thread.AsyncThreadManager;
import org.grain.threadwebsocket.ThreadWSManager;
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
			// 初始化线程消息
			AsyncThreadManager.init(100, 10, 3, 0, grainLog1);
			AsyncThreadManager.start();
			MsgManager.init(true, grainLog1);
			WSManager.init(grainLog1);
			// 映射操作码解析类,如果不设置线程优先级则随机线程优先级
			ThreadWSManager.addThreadMapping("testc", TestC.class, new int[] { 2, 1 });
			ThreadWSManager.addThreadMapping("tests", TestS.class, null);

			// 注册tcp回调函数
			TestWSService testWSService = new TestWSService();
			WSManager.addWSListener(testWSService);

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
