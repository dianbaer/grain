package service;

import java.util.HashMap;
import java.util.Map;

import http.HOpCodeDemoServer;
import http.HSession;
import http.HttpPacket;
import http.IHttpListener;
import protobuf.http.DemoServerProto.DemoServerC;
import protobuf.http.DemoServerProto.DemoServerS;

public class DemoService implements IHttpListener {

	@Override
	public Map<Integer, String> getHttps() throws Exception {
		HashMap<Integer, String> map = new HashMap<>();
		map.put(HOpCodeDemoServer.DEMO_SERVER, "demoServerHandle");
		return map;
	}

	@Override
	public Object getInstance() {
		return this;
	}

	public HttpPacket demoServerHandle(HSession hSession) {
		DemoServerC message = (DemoServerC) hSession.httpPacket.getData();

		DemoServerS.Builder builder = DemoServerS.newBuilder();
		builder.setHOpCode(hSession.headParam.hOpCode);
		HttpPacket packet = new HttpPacket(hSession.headParam.hOpCode, builder.build());
		return packet;
	}

}
