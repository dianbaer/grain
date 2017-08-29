package tcp;

import protobuf.tcp.TestOuterClass.Test;

public class TOpCodeRPCServer extends TOpCode {
	public static int TEST = 100;

	public static void init() {
		tOpCodeMap.put(TEST, Test.class);
	}
}
