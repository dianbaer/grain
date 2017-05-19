package mina.distributedlock;

import distributedlock.CenterDistributedLockType;

public class TestDistributedLockServerCenter implements IDistributedLockServer {

	@Override
	public String[] getTypes() {
		return new String[] { CenterDistributedLockType.CENTER_TEST1, CenterDistributedLockType.CENTER_TEST2 };
	}

}
