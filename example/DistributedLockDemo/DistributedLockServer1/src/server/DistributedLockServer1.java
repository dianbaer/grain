package server;

import distributedlock.Server1DistributedLockType;
import mina.distributedlock.IDistributedLockServer;

public class DistributedLockServer1 implements IDistributedLockServer {

	@Override
	public String[] getTypes() {
		return new String[] { Server1DistributedLockType.SERVER1_TEST1, Server1DistributedLockType.SERVER1_TEST2 };
	}

}
