package tcp;

import protobuf.tcp.Test1OuterClass.Test1;

public class TOpCodeRPCServer1 extends TOpCode {
	public static int TEST1 = 1000;

	public static void init() {
		tOpCodeMap.put(TEST1, Test1.class);
	}
}
