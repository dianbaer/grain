package mina.waitlock;

import java.util.concurrent.atomic.AtomicInteger;

import tcp.TcpPacket;

public class WaitLock {
	private static AtomicInteger atomicInteger = new AtomicInteger(0);
	private int instanceId;
	private TcpPacket tcpPacket;

	public WaitLock() {
		instanceId = atomicInteger.incrementAndGet();
	}

	public int getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(int instanceId) {
		this.instanceId = instanceId;
	}

	public TcpPacket getTcpPacket() {
		return tcpPacket;
	}

	public void setTcpPacket(TcpPacket tcpPacket) {
		this.tcpPacket = tcpPacket;
	}

}
