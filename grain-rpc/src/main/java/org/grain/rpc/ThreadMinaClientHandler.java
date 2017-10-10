package org.grain.rpc;

import org.apache.mina.core.session.IoSession;
import org.grain.tcp.MinaClient;
import org.grain.tcp.MinaConfig;
import org.grain.tcp.MinaHandler;
import org.grain.tcp.TcpMsg;
import org.grain.tcp.TcpPacket;
import org.grain.threadmsg.ThreadMsgManager;

public class ThreadMinaClientHandler extends MinaHandler {

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		super.messageReceived(session, message);
		if (MinaConfig.log != null) {
			MinaConfig.log.info("minaclient messageReceived");
		}
		// 派发tcp
		ThreadTcpManager.dispatchTcp((TcpPacket) message);
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
		ThreadMsgManager.dispatchThreadMsg(TcpMsg.MINA_SERVER_DISCONNECT, this.name, session);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
		if (MinaConfig.log != null) {
			MinaConfig.log.info("minaclient sessionCreated");
		}
		// 发布与服务器链接成功的消息
		ThreadMsgManager.dispatchThreadMsg(TcpMsg.MINA_SERVER_CONNECTED, this.name, session);
	}

}
