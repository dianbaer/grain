package server;

import mina.IMinaClientName;

public class MinaServerName implements IMinaClientName {
	public static String DISTRIBUTEDLOCK_SERVER = "DistributedLockServer";
	public static String DISTRIBUTEDLOCK_SERVER1 = "DistributedLockServer1";

	@Override
	public String[] getClientNames() {
		return new String[] { DISTRIBUTEDLOCK_SERVER, DISTRIBUTEDLOCK_SERVER1 };
	}

}
