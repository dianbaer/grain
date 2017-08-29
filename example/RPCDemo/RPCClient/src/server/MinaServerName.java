package server;

import mina.IMinaClientName;

public class MinaServerName implements IMinaClientName {
	public static String RPC_SERVER = "RPCServer";
	

	@Override
	public String[] getClientNames() {
		return new String[] { RPC_SERVER};
	}

}
