package http;

import java.util.Map;

public interface IHttpListener {
	public Map<Integer, String> getHttps() throws Exception;

	public Object getInstance();
}
