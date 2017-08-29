package service;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

import distributedlock.Server1DistributedLockType;
import distributedlock.ServerDistributedLockType;
import init.Init;
import mina.MinaClientService;
import mina.distributedlock.DistributedLockClient;
import msg.IMsgListener;
import msg.MsgOpCode;
import msg.MsgPacket;
import protobuf.msg.MinaMsg.MinaServerCanUse;
import server.MinaServerName;

public class DistributedLockClientTestService implements IMsgListener {
	public int aaa = 1;

	@Override
	public Map<String, String> getMsgs() throws Exception {
		HashMap<String, String> map = new HashMap<>();
		map.put(MsgOpCode.MINA_SERVER_CAN_USE, "serverCanUse");
		return map;
	}

	@Override
	public Object getInstance() {
		return this;
	}

	public void serverCanUse(MsgPacket msgPacket) {
		MinaServerCanUse message = (MinaServerCanUse) msgPacket.getData();
		if (message.getName().equals(MinaServerName.DISTRIBUTEDLOCK_SERVER)) {
			int lockId = DistributedLockClient.getLock("111", ServerDistributedLockType.SERVER_TEST1);
			if (lockId == 0) {
				return;
			}
			DistributedLockClient.unLock("111", ServerDistributedLockType.SERVER_TEST1, lockId);
		} else if (message.getName().equals(MinaServerName.DISTRIBUTEDLOCK_SERVER1)) {
			int lockId = DistributedLockClient.getLock("111", Server1DistributedLockType.SERVER1_TEST1);
			if (lockId == 0) {
				return;
			}
			DistributedLockClient.unLock("111", Server1DistributedLockType.SERVER1_TEST1, lockId);
		}

	}
}
