package server;

import http.HOpCodeDemoServer;
import init.IExpand;
import init.Init;
import service.DemoService;

public class Expand implements IExpand {

	@Override
	public void init() throws Exception {
		HOpCodeDemoServer.init();
		Init.registerService(DemoService.class);
	}

	@Override
	public void threadInit() throws Exception {

	}
}
