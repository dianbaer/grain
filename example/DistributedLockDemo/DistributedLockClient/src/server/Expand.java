package server;

import init.IExpand;
import init.Init;
import service.DistributedLockClientTestService;

public class Expand implements IExpand {

	@Override
	public void init() throws Exception {
		Init.registerService(DistributedLockClientTestService.class);
	}

	@Override
	public void threadInit() throws Exception {

	}
}
