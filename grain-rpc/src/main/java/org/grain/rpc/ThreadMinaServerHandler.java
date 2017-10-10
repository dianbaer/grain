package org.grain.rpc;

import org.apache.mina.core.session.IoSession;
import org.grain.tcp.MinaConfig;
import org.grain.tcp.MinaHandler;
import org.grain.tcp.TcpMsg;
import org.grain.tcp.TcpPacket;
import org.grain.threadmsg.ThreadMsgManager;

public class ThreadMinaServerHandler extends MinaHandler {

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		super.messageReceived(session, message);
		if (MinaConfig.log != null) {
			MinaConfig.log.info("minaserver messageReceived");
		}
		ThreadTcpManager.dispatchTcp((TcpPacket) message);
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
