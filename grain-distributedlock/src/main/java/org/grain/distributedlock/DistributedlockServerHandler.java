package org.grain.distributedlock;

import org.apache.mina.core.session.IoSession;
import org.grain.rpc.ThreadTcpManager;
import org.grain.tcp.MinaConfig;
import org.grain.tcp.MinaHandler;
import org.grain.tcp.TcpMsg;
import org.grain.tcp.TcpPacket;
import org.grain.threadmsg.ThreadMsgManager;

public class DistributedlockServerHandler extends MinaHandler {

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		super.messageReceived(session, message);
		if (MinaConfig.log != null) {
			MinaConfig.log.info("minaserver messageReceived");
		}
		TcpPacket pt = (TcpPacket) message;
		// 如果是获取锁，需要汇集到一个线程
		if (pt.gettOpCode() == DistributedlockTCode.DISTRIBUTED_LOCK_C1) {
			ThreadMsgManager.dispatchThreadMsg(DistributedlockMsg.MERGE_TCPPACKET, message, null);
		} else {
			// 其他情况不需要
			ThreadTcpManager.dispatchTcp(pt);
		}

	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
		if (MinaConfig.log != null) {
			MinaConfig.log.warn("minaserver sessionClosed");
		}
		ThreadMsgManager.dispatchThreadMsg(TcpMsg.MINA_CLIENT_DISCONNECT, null, session);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
		if (MinaConfig.log != null) {
			MinaConfig.log.info("minaserver sessionCreated");
		}
		ThreadMsgManager.dispatchThreadMsg(TcpMsg.MINA_CLIENT_CREATE_CONNECT, null, session);
	}

}
