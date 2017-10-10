package org.grain.httpserver;

public interface IHttpFilter {
	/**
	 * 实现此接口，可以进行http预处理进行请求过滤
	 * 
	 * @param httpPacket
	 * @return
	 */
	public boolean httpFilter(HttpPacket httpPacket);
}
