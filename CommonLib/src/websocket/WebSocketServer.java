package websocket;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import msg.MsgManager;
import msg.MsgOpCode;
import msg.MsgPacket;
import protobuf.msg.WebSocketMsg.WebSocketClientCreateConnect;
import protobuf.msg.WebSocketMsg.WebSocketClientDisConnect;
import ws.WsPacket;

@ServerEndpoint(value = "/ws", decoders = { WebSocketDeCoder.class }, encoders = { WebSocketEnCoder.class })
public class WebSocketServer {
	@OnOpen
	public void onOpen(Session session) {
		WebSocketClientCreateConnect.Builder builder = WebSocketClientCreateConnect.newBuilder();
		builder.setName("xxxxxx");
		MsgPacket msgPacket = new MsgPacket(MsgOpCode.WEBSOCKET_CLIENT_CREATE_CONNECT, builder.build(), MsgManager.USE_MSG_MONITOR);
		msgPacket.setOtherData(session);
		MsgManager.dispatchMsg(msgPacket);
	}

	@OnMessage
	public void onMessage(Session session, WsPacket wsPacket) {
		wsPacket.session = session;
		WSManager.dispatchWS(wsPacket);
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		WebSocketClientDisConnect.Builder builder = WebSocketClientDisConnect.newBuilder();
		builder.setName("xxxxxx");
		MsgPacket msgPacket = new MsgPacket(MsgOpCode.WEBSOCKET_CLIENT_DISCONNECT, builder.build(), MsgManager.USE_MSG_MONITOR);
		msgPacket.setOtherData(session);
		MsgManager.dispatchMsg(msgPacket);
	}

	@OnError
	public void onError(Session session, Throwable throwable) {

	}
}
