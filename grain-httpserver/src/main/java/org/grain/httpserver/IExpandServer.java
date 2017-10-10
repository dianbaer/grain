package org.grain.httpserver;

import javax.servlet.http.HttpServlet;

public interface IExpandServer {
	public void init(HttpServlet servlet) throws Exception;
}
