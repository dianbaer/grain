package org.grain.distributedlock;

import java.util.HashMap;
import java.util.Map;

import org.grain.msg.IMsgListener;
import org.grain.msg.MsgPacket;
import org.grain.tcp.TcpPacket;

public class MergeTCPService implements IMsgListener {

	@Override
	public Map<String, String> getMsgs() throws Exception {
		HashMap<String, String> map = new HashMap<>();
		map.put(DistributedlockMsg.MERGE_TCPPACKET, "mergeTcppacketHandle");
		return map;
	}

	/**
	 * 合并tcp消息包归到一个线程，再进行分发
	 * 
	 * @param msgPacket
	 */
	public void mergeTcppacketHandle(MsgPacket msgPacket) {
		// 进行分发
		DistributedlockTcpManager.dispatchTcp((TcpPacket) msgPacket.getData());
	}

}
