package server;

import init.IExpand;
import init.Init;
import service.RPCServerTestService;
import tcp.TOpCodeRPCServer;

public class Expand implements IExpand {

	@Override
	public void init() throws Exception {
		TOpCodeRPCServer.init();
		Init.registerService(RPCServerTestService.class);
	}

	@Override
	public void threadInit() throws Exception {

	}
}
