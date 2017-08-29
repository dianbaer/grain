package server;

import init.IExpand;
import init.Init;
import service.RPCServer1TestService;
import tcp.TOpCodeRPCServer1;

public class Expand implements IExpand {

	@Override
	public void init() throws Exception {
		TOpCodeRPCServer1.init();
		Init.registerService(RPCServer1TestService.class);
	}

	@Override
	public void threadInit() throws Exception {

	}
}
