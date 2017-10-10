package org.grain.httpserver;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grain.log.ILog;

public class InitHttpServer extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		long t1 = System.currentTimeMillis();
		try {
			super.init();
			ServletContext servletContext = getServletContext();
			// 日志初始化
			String ILogClass = servletContext.getInitParameter("ILog");
			// 初始化日志
			if (ILogClass != null) {
				HttpConfig.log = (ILog) Class.forName(ILogClass).newInstance();
			}
			// 获取项目路径和项目名
			String realPath = servletContext.getRealPath("/");
			realPath = realPath.substring(0, realPath.length() - 1);
			int pos = -1;
			if (realPath.lastIndexOf("/") != -1) {
				pos = realPath.lastIndexOf("/");
			} else {
				pos = realPath.lastIndexOf("\\");
			}
			HttpConfig.PROJECT_PATH = realPath.substring(0, pos);
			HttpConfig.PROJECT_NAME = realPath.substring(pos + 1);
			if (HttpConfig.log != null) {
				HttpConfig.log.info("项目根目录为：" + HttpConfig.PROJECT_PATH);
				HttpConfig.log.info("项目名为：" + HttpConfig.PROJECT_NAME);
			}
			// 初始化http服务器
			HttpManager.init();
			String IUploadProgressClass = servletContext.getInitParameter("IUploadProgress");
			if (IUploadProgressClass != null) {
				HttpConfig.UPLOAD_PROGRESS_CLASS = (Class<IUploadProgress>) Class.forName(IUploadProgressClass);
			}

			// 初始化扩展业务
			String ExpandClass = servletContext.getInitParameter("Expand");
			if (ExpandClass != null) {
				Class.forName(ExpandClass).newInstance();
			}
			long t2 = System.currentTimeMillis();
			long t = t2 - t1;
			if (HttpConfig.log != null) {
				HttpConfig.log.info("CommonServlet初始化完成，耗时：" + t + "毫秒");
			}
		} catch (Throwable e) {
			long t2 = System.currentTimeMillis();
			long t = t2 - t1;
			if (HttpConfig.log != null) {
				HttpConfig.log.error("CommonServlet初始化失败，耗时" + t + "毫秒", e);
			}
			System.exit(0);
		}

	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpPacket httpPacket = null;
		try {
			String url = request.getRequestURI();
			if (HttpConfig.log != null) {
				HttpConfig.log.info("InitHttpServer:" + HttpConfig.PROJECT_NAME + "请求地址为：" + url);
			}
			httpPacket = HttpManager.filter(request, response);
			if (httpPacket == null) {
				if (HttpConfig.log != null) {
					HttpConfig.log.warn("过滤器范围的hsession为空");
				}
				return;
			}
			httpPacket.putMonitor("所有过滤器过滤完成");
			HttpManager.handleHttp(httpPacket);
		} catch (Exception e) {
			if (HttpConfig.log != null) {
				HttpConfig.log.error("http处理异常", e);
			}
		} finally {
			if (httpPacket != null) {
				if (HttpConfig.log != null) {
					HttpConfig.log.info(httpPacket.runMonitor.toString(httpPacket.gethOpCode()));
				}
				httpPacket.clear();
			}
		}
	}

}
