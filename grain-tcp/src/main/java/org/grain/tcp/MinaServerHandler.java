package org.grain.tcp;

import org.apache.mina.core.session.IoSession;
import org.grain.msg.MsgManager;

public class MinaServerHandler extends MinaHandler {

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		super.messageReceived(session, message);
		if (MinaConfig.log != null) {
			MinaConfig.log.info("minaserver messageReceived");
		}
		TcpManager.dispatchTcp((TcpPacket) message);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
		if (MinaConfig.log != null) {
			MinaConfig.log.warn("minaserver sessionClosed");
		}
		MsgManager.dispatchMsg(TcpMsg.MINA_CLIENT_DISCONNECT, null, session);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
		if (MinaConfig.log != null) {
			MinaConfig.log.info("minaserver sessionCreated");
		}
		MsgManager.dispatchMsg(TcpMsg.MINA_CLIENT_CREATE_CONNECT, null, session);
	}

}
