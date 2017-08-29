package server;

import java.util.HashMap;

import distributedlock.Server1DistributedLockType;
import distributedlock.ServerDistributedLockType;
import mina.distributedlock.IDistributedLockClient;

public class DistributedLockClient implements IDistributedLockClient {

	@Override
	public HashMap<String, String> getTypesToServer() {
		HashMap<String, String> map = new HashMap<>();
		map.put(Server1DistributedLockType.SERVER1_TEST1, MinaServerName.DISTRIBUTEDLOCK_SERVER1);
		map.put(Server1DistributedLockType.SERVER1_TEST2, MinaServerName.DISTRIBUTEDLOCK_SERVER1);
		map.put(ServerDistributedLockType.SERVER_TEST1, MinaServerName.DISTRIBUTEDLOCK_SERVER);
		map.put(ServerDistributedLockType.SERVER_TEST2, MinaServerName.DISTRIBUTEDLOCK_SERVER);
		return map;
	}

}
