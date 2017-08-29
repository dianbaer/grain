package server;

import init.Init;
import log.LogManager;

public class RPCServerMain {
	public static void main(String[] args) {
		long t1 = System.currentTimeMillis();
		try {
			Init.init("RPCServer.properties");
			long t2 = System.currentTimeMillis();
			long t = t2 - t1;
			LogManager.initLog.info("RPCServerMain初始化完成，耗时：" + t + "毫秒");
		} catch (Throwable e) {
			long t2 = System.currentTimeMillis();
			long t = t2 - t1;
			LogManager.initLog.error("RPCServerMain初始化失败，耗时" + t + "毫秒", e);
			System.exit(0);
		}
	}
}
