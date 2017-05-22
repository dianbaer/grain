package msg;

import java.util.Map;

public interface IMsgListener {
	public Map<String, String> getMsgs() throws Exception;

	public Object getInstance();
}
