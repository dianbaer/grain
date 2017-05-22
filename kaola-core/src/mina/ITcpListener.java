package mina;

import java.util.Map;

public interface ITcpListener {
	public Map<Integer, String> getTcps() throws Exception;

	public Object getInstance();
}
