package server;

import init.IExpand;
import tcp.TOpCodeRPCServer;

public class Expand implements IExpand {

	@Override
	public void init() throws Exception {
		TOpCodeRPCServer.init();
	}

	@Override
	public void threadInit() throws Exception {

	}
}
