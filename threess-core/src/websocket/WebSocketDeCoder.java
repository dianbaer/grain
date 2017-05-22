package websocket;

import java.nio.ByteBuffer;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import ws.WsPacket;

public class WebSocketDeCoder implements Decoder.Binary<WsPacket> {

	@Override
	public void destroy() {

	}

	@Override
	public void init(EndpointConfig arg0) {

	}

	@Override
	public WsPacket decode(ByteBuffer arg0) throws DecodeException {
		return WSCodeUtil.decodeJson(arg0);
	}

	@Override
	public boolean willDecode(ByteBuffer arg0) {

		return true;
	}

}
