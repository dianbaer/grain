package org.grain.websokcetlib;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class WebSocketEnCoder implements Encoder.Text<WsPacket> {

	@Override
	public void destroy() {

	}

	@Override
	public void init(EndpointConfig arg0) {

	}

	@Override
	public String encode(WsPacket arg0) throws EncodeException {
		return WSCodeUtil.encodeJson(arg0);
	}

}
