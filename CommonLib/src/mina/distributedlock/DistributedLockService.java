package mina.distributedlock;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

import mina.ITcpListener;
import mina.waitlock.WaitLockManager;
import protobuf.tcp.DistributedLock.DistributedLockS1;
import tcp.TOpCode;
import tcp.TcpPacket;

public class DistributedLockService implements ITcpListener {

	@Override
	public Map<Integer, String> getTcps() throws Exception {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(TOpCode.DISTRIBUTED_LOCK_C1, "distributedLockC1");
		return map;
	}

	@Override
	public Object getInstance() {
		return this;
	}

	public void distributedLockC1(TcpPacket tcpPacket) {
		IoSession ioSession = (IoSession) tcpPacket.session;
		DistributedLockS1.Builder builder = DistributedLockS1.newBuilder();
		builder.setResult(true);
		TcpPacket sendPacket = new TcpPacket(TOpCode.DISTRIBUTED_LOCK_S1, builder.build());
		sendPacket.unlockedId = tcpPacket.lockedId;
		WaitLockManager.lock(ioSession, sendPacket);
	}

}
