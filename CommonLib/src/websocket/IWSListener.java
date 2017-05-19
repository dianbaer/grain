package websocket;

import java.util.Map;

public interface IWSListener {
	public Map<Integer, String> getWSs() throws Exception;

	public Object getInstance();
}
