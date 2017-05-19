package tcp;

import java.util.HashMap;

import protobuf.tcp.DistributedLock.DistributedLockC1;
import protobuf.tcp.DistributedLock.DistributedLockC2;
import protobuf.tcp.DistributedLock.DistributedLockS1;

public class TOpCode {
	public static HashMap<Integer, Class<?>> tOpCodeMap = new HashMap<Integer, Class<?>>();
	public static HashMap<Integer, int[]> tOpCodeType = new HashMap<Integer, int[]>();
	public static int DISTRIBUTED_LOCK_C1 = 1;
	public static int DISTRIBUTED_LOCK_S1 = 2;
	public static int DISTRIBUTED_LOCK_C2 = 3;
	public static void init(){
		tOpCodeMap.put(DISTRIBUTED_LOCK_C1, DistributedLockC1.class);
		tOpCodeMap.put(DISTRIBUTED_LOCK_S1, DistributedLockS1.class);
		tOpCodeMap.put(DISTRIBUTED_LOCK_C2, DistributedLockC2.class);
	}
}
