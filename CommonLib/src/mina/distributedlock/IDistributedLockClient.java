package mina.distributedlock;

import java.util.HashMap;

public interface IDistributedLockClient {
	public HashMap<String, String> getTypesToServer();
}
