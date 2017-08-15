package http;

import protobuf.http.DemoServerProto.DemoServerC;
import protobuf.http.DemoServerProto.DemoServerS;

public class HOpCodeDemoServer extends HOpCode {

	public static int DEMO_SERVER = 1;

	public static void init() {

		Class<?>[] sendAndReturn = new Class[2];
		sendAndReturn[0] = DemoServerC.class;
		sendAndReturn[1] = DemoServerS.class;
		hOpCodeMap.put(DEMO_SERVER, sendAndReturn);

	}
}
