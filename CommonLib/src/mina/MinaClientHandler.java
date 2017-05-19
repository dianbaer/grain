package mina;

import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import log.LogManager;
import msg.MsgManager;
import msg.MsgOpCode;
import msg.MsgPacket;
import protobuf.msg.MinaMsg.MinaServerConnected;
import protobuf.msg.MinaMsg.MinaServerDisConnect;
import tcp.TcpPacket;

public class MinaClientHandler extends IoHandlerAdapter {
	public IoConnector ioConnector;
	public String name;

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		super.exceptionCaught(session, cause);
		LogManager.minaLog.info("minaclient exceptionCaught");
	}

	@Override
	public void inputClosed(IoSession session) throws Exception {
		super.inputClosed(session);
		LogManager.minaLog.info("minaclient inputClosed");
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		super.messageReceived(session, message);
		LogManager.minaLog.info("minaclient messageReceived");
		TcpManager.dispatchTcp((TcpPacket) message);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		super.messageSent(session, message);
		LogManager.minaLog.info("minaclient messageSent");
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
		LogManager.minaLog.warn("minaclient sessionClosed");
		MinaClient.getInstance().ioConnectorStateMap.put(ioConnector, false);

		MinaServerDisConnect.Builder builder = MinaServerDisConnect.newBuilder();
		builder.setName(this.name);
		MsgPacket msgPacket = new MsgPacket(MsgOpCode.MINA_SERVER_DISCONNECT, builder.build(), MsgManager.USE_MSG_MONITOR);
		msgPacket.setOtherData(session);
		MsgManager.dispatchMsg(msgPacket);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
		LogManager.minaLog.info("minaclient sessionCreated");

		MinaServerConnected.Builder builder = MinaServerConnected.newBuilder();
		builder.setName(this.name);
		MsgPacket msgPacket = new MsgPacket(MsgOpCode.MINA_SERVER_CONNECTED, builder.build(), MsgManager.USE_MSG_MONITOR);
		msgPacket.setOtherData(session);
		MsgManager.dispatchMsg(msgPacket);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		super.sessionIdle(session, status);
		LogManager.minaLog.info("minaclient sessionIdle");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		super.sessionOpened(session);
		LogManager.minaLog.info("minaclient sessionOpened");
	}

}
