package mina.distributedlock;

import java.util.HashMap;

import distributedlock.CenterDistributedLockType;
import distributedlock.OtherDistributedLockType;
import mina.TestMinaClientName;

public class TestDistributedLockClient implements IDistributedLockClient {

	@Override
	public HashMap<String, String> getTypesToServer() {
		HashMap<String, String> map = new HashMap<>();
		map.put(CenterDistributedLockType.CENTER_TEST1, TestMinaClientName.CENTER);
		map.put(CenterDistributedLockType.CENTER_TEST2, TestMinaClientName.CENTER);
		map.put(OtherDistributedLockType.OTHER_TEST1, TestMinaClientName.OTHER);
		map.put(OtherDistributedLockType.OTHER_TEST2, TestMinaClientName.OTHER);
		return map;
	}

}
