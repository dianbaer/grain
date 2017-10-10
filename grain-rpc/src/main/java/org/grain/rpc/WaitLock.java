package org.grain.rpc;

import java.util.concurrent.atomic.AtomicInteger;

import org.grain.tcp.TcpPacket;

public class WaitLock {
	/**
	 * 自增
	 */
	private static AtomicInteger atomicInteger = new AtomicInteger(0);
	/**
	 * 对于每个进程都是唯一的id
	 */
	private int instanceId;
	/**
	 * 消息包
	 */
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
