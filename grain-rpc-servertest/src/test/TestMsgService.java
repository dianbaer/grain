package test;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.grain.msg.IMsgListener;
import org.grain.msg.MsgPacket;
import org.grain.rpc.WaitLockManager;
import org.grain.tcp.TcpMsg;
import org.grain.tcp.TcpPacket;

import protobuf.tcp.Test.RPCTestC;
import protobuf.tcp.Test.RPCTestS;

public class TestMsgService implements IMsgListener {

	@Override
	public Map<String, String> getMsgs() throws Exception {
		HashMap<String, String> map = new HashMap<>();
		map.put(TcpMsg.MINA_CLIENT_CREATE_CONNECT, "onClientConnected");
		return map;
	}

	public void onClientConnected(MsgPacket msgPacket) {
		IoSession session = (IoSession) msgPacket.getOtherData();
		System.out.println("接到消息：" + msgPacket.getMsgOpCode());
		RPCTestS.Builder builder = RPCTestS.newBuilder();
		builder.setName("RPC你好啊");
		TcpPacket pt = new TcpPacket(TestTCode.TEST_RPC_SERVER, builder.build());

		TcpPacket ptReturn = WaitLockManager.lock(session, pt);
		RPCTestC rpcTestC = (RPCTestC) ptReturn.getData();
		System.out.println("接到RPC消息：" + rpcTestC.getName());

	}

}
