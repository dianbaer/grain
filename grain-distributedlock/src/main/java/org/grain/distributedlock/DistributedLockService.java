package org.grain.distributedlock;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.grain.distributedlock.tcp.DistributedLock.DistributedLockS1;
import org.grain.tcp.ITcpListener;
import org.grain.tcp.TcpPacket;
import org.grain.rpc.WaitLockManager;

public class DistributedLockService implements ITcpListener {

	@Override
	public Map<Integer, String> getTcps() throws Exception {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(DistributedlockTCode.DISTRIBUTED_LOCK_C1, "distributedLockC1");
		return map;
	}

	/**
	 * 接受获取锁，然后阻塞等待锁客户端唤醒
	 * 
	 * @param tcpPacket
	 */
	public void distributedLockC1(TcpPacket tcpPacket) {
		IoSession ioSession = (IoSession) tcpPacket.session;
		DistributedLockS1.Builder builder = DistributedLockS1.newBuilder();
		builder.setResult(true);
		TcpPacket sendPacket = new TcpPacket(DistributedlockTCode.DISTRIBUTED_LOCK_S1, builder.build());
		sendPacket.unlockedId = tcpPacket.lockedId;
		WaitLockManager.lock(ioSession, sendPacket);
	}

}
