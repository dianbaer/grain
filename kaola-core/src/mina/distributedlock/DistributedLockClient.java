package mina.distributedlock;

import java.util.HashMap;

import org.apache.mina.core.session.IoSession;

import init.Init;
import log.LogManager;
import mina.MinaClientService;
import mina.waitlock.WaitLockManager;
import protobuf.tcp.DistributedLock.DistributedLockC1;
import protobuf.tcp.DistributedLock.DistributedLockC2;
import protobuf.tcp.DistributedLock.DistributedLockS1;
import tcp.TOpCode;
import tcp.TcpPacket;

public class DistributedLockClient {
	private static HashMap<String, String> lockToServer;

	public static void init(String distributedLockClientClass) throws Exception {
		IDistributedLockClient distributedLockClient = (IDistributedLockClient) Class.forName(distributedLockClientClass).newInstance();
		lockToServer = distributedLockClient.getTypesToServer();
	}

	public static int getLock(String key, String type) {
		String serverName = lockToServer.get(type);
		if (serverName == null) {
			LogManager.distributedlockLog.warn("类型为：" + type + "未找到对应服务器");
			return 0;
		}
		MinaClientService minaClientService = (MinaClientService) Init.getService(MinaClientService.class);
		IoSession ioSession = minaClientService.getServerIoSession(serverName);
		if (ioSession == null) {
			LogManager.distributedlockLog.warn("类型为：" + type + "，未初始化相应服务器或者与此服务器断开");
			return 0;
		}
		DistributedLockC1.Builder builder = DistributedLockC1.newBuilder();
		builder.setKey(key);
		builder.setType(type);
		TcpPacket tcpPacket = new TcpPacket(TOpCode.DISTRIBUTED_LOCK_C1, builder.build());
		TcpPacket returnPT = WaitLockManager.lock(ioSession, tcpPacket);
		if (returnPT == null) {
			return 0;
		}
		DistributedLockS1 distributedLockS1 = (DistributedLockS1) returnPT.getData();
		if (!distributedLockS1.getResult()) {
			return 0;
		}
		return returnPT.lockedId;
	}

	public static void unLock(String key, String type, int lockId) {
		String serverName = lockToServer.get(type);
		if (serverName == null) {
			LogManager.distributedlockLog.warn("类型为：" + type + "未找到对应服务器");
			return;
		}
		MinaClientService minaClientService = (MinaClientService) Init.getService(MinaClientService.class);
		IoSession ioSession = minaClientService.getServerIoSession(serverName);
		if (ioSession == null) {
			LogManager.distributedlockLog.warn("类型为：" + type + "，未初始化相应服务器或者与此服务器断开");
			return;
		}
		DistributedLockC2.Builder builder = DistributedLockC2.newBuilder();
		builder.setName("111");
		TcpPacket tcpPacket = new TcpPacket(TOpCode.DISTRIBUTED_LOCK_C2, builder.build());
		tcpPacket.unlockedId = lockId;
		ioSession.write(tcpPacket);
	}
}
