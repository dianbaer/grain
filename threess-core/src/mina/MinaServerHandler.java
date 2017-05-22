package mina;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import log.LogManager;
import msg.MsgManager;
import msg.MsgOpCode;
import msg.MsgPacket;
import protobuf.msg.MinaMsg.MinaClientCreateConnect;
import protobuf.msg.MinaMsg.MinaClientDisConnect;
import tcp.TcpPacket;

public class MinaServerHandler extends IoHandlerAdapter {
	public IoAcceptor ioAcceptor;

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {

		super.exceptionCaught(session, cause);
		LogManager.minaLog.info("minaserver exceptionCaught");
	}

	@Override
	public void inputClosed(IoSession session) throws Exception {

		super.inputClosed(session);
		LogManager.minaLog.info("minaserver inputClosed");
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {

		super.messageReceived(session, message);
		LogManager.minaLog.info("minaserver messageReceived");
		TcpManager.dispatchTcp((TcpPacket) message);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {

		super.messageSent(session, message);
		LogManager.minaLog.info("minaserver messageSent");
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {

		super.sessionClosed(session);
		LogManager.minaLog.warn("minaserver sessionClosed");

		MinaClientDisConnect.Builder builder = MinaClientDisConnect.newBuilder();
		builder.setName("xxxxxx");
		MsgPacket msgPacket = new MsgPacket(MsgOpCode.MINA_CLIENT_DISCONNECT, builder.build(), MsgManager.USE_MSG_MONITOR);
		msgPacket.setOtherData(session);
		MsgManager.dispatchMsg(msgPacket);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {

		super.sessionCreated(session);
		LogManager.minaLog.info("minaserver sessionCreated");

		MinaClientCreateConnect.Builder builder = MinaClientCreateConnect.newBuilder();
		builder.setName("xxxxxx");
		MsgPacket msgPacket = new MsgPacket(MsgOpCode.MINA_CLIENT_CREATE_CONNECT, builder.build(), MsgManager.USE_MSG_MONITOR);
		msgPacket.setOtherData(session);
		MsgManager.dispatchMsg(msgPacket);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {

		super.sessionIdle(session, status);
		LogManager.minaLog.info("minaserver sessionIdle");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		super.sessionOpened(session);
		LogManager.minaLog.info("minaserver sessionOpened");
	}

}
