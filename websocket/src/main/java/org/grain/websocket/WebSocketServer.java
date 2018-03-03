package org.grain.websocket;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.grain.msg.MsgManager;
import org.grain.websokcetlib.WSManager;
import org.grain.websokcetlib.WSMsg;
import org.grain.websokcetlib.WebSocketDeCoder;
import org.grain.websokcetlib.WebSocketEnCoder;
import org.grain.websokcetlib.WsPacket;

@ServerEndpoint(value = "/ws", decoders = { WebSocketDeCoder.class }, encoders = { WebSocketEnCoder.class })
public class WebSocketServer {
	@OnOpen
	public void onOpen(Session session) {

		try {
			MsgManager.dispatchMsg(WSMsg.WEBSOCKET_CLIENT_CREATE_CONNECT, null, session);
		} catch (Exception e) {
			if (WSManager.log != null) {
				WSManager.log.error("MsgManager.dispatchMsg error", e);
			}
		}
	}

	@OnMessage
	public void onMessage(Session session, WsPacket wsPacket) {
		wsPacket.session = session;
		WSManager.dispatchWS(wsPacket);
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		try {
			MsgManager.dispatchMsg(WSMsg.WEBSOCKET_CLIENT_DISCONNECT, null, session);
		} catch (Exception e) {
			if (WSManager.log != null) {
				WSManager.log.error("MsgManager.dispatchMsg error", e);
			}
		}
	}
}
