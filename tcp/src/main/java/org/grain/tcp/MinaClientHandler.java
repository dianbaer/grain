package org.grain.tcp;

import org.apache.mina.core.session.IoSession;
import org.grain.msg.MsgManager;

public class MinaClientHandler extends MinaHandler {

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		super.messageReceived(session, message);
		if (MinaConfig.log != null) {
			MinaConfig.log.info("minaclient messageReceived");
		}
		// 派发tcp
		TcpManager.dispatchTcp((TcpPacket) message);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
		if (MinaConfig.log != null) {
			MinaConfig.log.warn("minaclient sessionClosed");
		}
		// 设置链接已断开，断线过会儿会断线重连
		MinaClient.getInstance().ioConnectorStateMap.put(ioConnector, false);
		// 发布与服务器断开的消息
		MsgManager.dispatchMsg(TcpMsg.MINA_SERVER_DISCONNECT, this.name, session);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
		if (MinaConfig.log != null) {
			MinaConfig.log.info("minaclient sessionCreated");
		}
		// 发布与服务器链接成功的消息
		MsgManager.dispatchMsg(TcpMsg.MINA_SERVER_CONNECTED, this.name, session);
	}

}
