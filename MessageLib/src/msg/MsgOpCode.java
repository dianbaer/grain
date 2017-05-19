package msg;

import java.util.HashMap;

import protobuf.msg.MinaMsg.MinaClientCreateConnect;
import protobuf.msg.MinaMsg.MinaClientDisConnect;
import protobuf.msg.MinaMsg.MinaServerCanUse;
import protobuf.msg.MinaMsg.MinaServerConnected;
import protobuf.msg.MinaMsg.MinaServerDisConnect;
import protobuf.msg.WebSocketMsg.WebSocketClientCreateConnect;
import protobuf.msg.WebSocketMsg.WebSocketClientDisConnect;

public class MsgOpCode {
	public static HashMap<String, Class<?>> msgOpCodeMap = new HashMap<String, Class<?>>();
	// 隶属线程，优先级
	public static HashMap<String, int[]> msgOpcodeType = new HashMap<String, int[]>();
	public static String MINA_SERVER_CONNECTED = "MINA_SERVER_CONNECTED";
	public static String MINA_SERVER_DISCONNECT = "MINA_SERVER_DISCONNECT";
	public static String MINA_CLIENT_CREATE_CONNECT = "MINA_CLIENT_CREATE_CONNECT";
	public static String MINA_CLIENT_DISCONNECT = "MINA_CLIENT_DISCONNECT";
	public static String MINA_SERVER_CAN_USE = "MINA_SERVER_CAN_USE";

	public static String WEBSOCKET_CLIENT_CREATE_CONNECT = "WEBSOCKET_CLIENT_CREATE_CONNECT";
	public static String WEBSOCKET_CLIENT_DISCONNECT = "WEBSOCKET_CLIENT_DISCONNECT";

	public static void init() {
		msgOpCodeMap.put(MINA_SERVER_CONNECTED, MinaServerConnected.class);
		msgOpcodeType.put(MINA_SERVER_CONNECTED, new int[] { 1, 1 });
		msgOpCodeMap.put(MINA_SERVER_DISCONNECT, MinaServerDisConnect.class);
		msgOpcodeType.put(MINA_SERVER_DISCONNECT, new int[] { 1, 1 });
		msgOpCodeMap.put(MINA_CLIENT_CREATE_CONNECT, MinaClientCreateConnect.class);
		msgOpcodeType.put(MINA_CLIENT_CREATE_CONNECT, new int[] { 1, 1 });
		msgOpCodeMap.put(MINA_CLIENT_DISCONNECT, MinaClientDisConnect.class);
		msgOpcodeType.put(MINA_CLIENT_DISCONNECT, new int[] { 1, 1 });
		msgOpCodeMap.put(MINA_SERVER_CAN_USE, MinaServerCanUse.class);
		msgOpcodeType.put(MINA_SERVER_CAN_USE, new int[] { 1, 1 });

		msgOpCodeMap.put(WEBSOCKET_CLIENT_CREATE_CONNECT, WebSocketClientCreateConnect.class);
		msgOpcodeType.put(WEBSOCKET_CLIENT_CREATE_CONNECT, new int[] { 1, 1 });
		msgOpCodeMap.put(WEBSOCKET_CLIENT_DISCONNECT, WebSocketClientDisConnect.class);
		msgOpcodeType.put(WEBSOCKET_CLIENT_DISCONNECT, new int[] { 1, 1 });
	}
}
