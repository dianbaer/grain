package server;

import distributedlock.ServerDistributedLockType;
import mina.distributedlock.IDistributedLockServer;

public class DistributedLockServer implements IDistributedLockServer {

	@Override
	public String[] getTypes() {
		return new String[] { ServerDistributedLockType.SERVER_TEST1, ServerDistributedLockType.SERVER_TEST2 };
	}

}
