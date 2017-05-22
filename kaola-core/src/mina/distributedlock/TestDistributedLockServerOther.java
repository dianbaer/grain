package mina.distributedlock;

import distributedlock.OtherDistributedLockType;

public class TestDistributedLockServerOther implements IDistributedLockServer {

	@Override
	public String[] getTypes() {
		return new String[] { OtherDistributedLockType.OTHER_TEST1, OtherDistributedLockType.OTHER_TEST2 };
	}

}
