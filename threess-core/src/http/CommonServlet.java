package http;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import init.Init;
import log.LogManager;

public class CommonServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		long t1 = System.currentTimeMillis();
		LogManager.initLog.info("CommonServlet初始化");
		try {
			super.init();
			ServletContext servletContext = getServletContext();
			String realPath = servletContext.getRealPath("/");
			HttpConfig.PROJECT_PATH = realPath;
			realPath = realPath.substring(0, realPath.length() - 1);
			int pos = -1;
			if (realPath.lastIndexOf("/") != -1) {
				pos = realPath.lastIndexOf("/");
				LogManager.initLog.info("获取/的index");
			} else {
				pos = realPath.lastIndexOf("\\");
				LogManager.initLog.info("获取\\的index");
			}
			HttpConfig.PROJECT_PATH = realPath.substring(0, pos);
			HttpConfig.PROJECT_NAME = realPath.substring(pos + 1);
			LogManager.initLog.info("项目根目录为：" + HttpConfig.PROJECT_PATH);
			LogManager.initLog.info("项目名为：" + HttpConfig.PROJECT_NAME);
			String configFileName = servletContext.getInitParameter("configFileName");
			LogManager.initLog.info("配置文件为：" + configFileName);
			Init.init(configFileName);
			long t2 = System.currentTimeMillis();
			long t = t2 - t1;
			LogManager.initLog.info("CommonServlet初始化完成，耗时：" + t + "毫秒");
		} catch (Throwable e) {
			long t2 = System.currentTimeMillis();
			long t = t2 - t1;
			LogManager.initLog.error("CommonServlet初始化失败，耗时" + t + "毫秒", e);
			System.exit(0);
		}

	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HSession hSession = null;
		try {
			String url = request.getRequestURI();
			LogManager.httpLog.info("CommonServlet:" + HttpConfig.PROJECT_NAME + "请求地址为：" + url);
			hSession = HttpFilterManager.filter(request, response);
			if (hSession == null) {
				LogManager.httpLog.warn("过滤器范围的hsession为空");
				return;
			}
			hSession.putMonitor("所有过滤器过滤完成");
			HttpManager.handleHttp(hSession);
		} catch (Throwable e) {
			LogManager.httpLog.error("http处理异常", e);
		}
	}

}
