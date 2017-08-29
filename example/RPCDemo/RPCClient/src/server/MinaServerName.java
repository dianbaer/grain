package server;

import mina.IMinaClientName;

public class MinaServerName implements IMinaClientName {
	public static String RPC_SERVER = "RPCServer";
	public static String RPC_SERVER1 = "RPCServer1";

	@Override
	public String[] getClientNames() {
		return new String[] { RPC_SERVER, RPC_SERVER1 };
	}

}
