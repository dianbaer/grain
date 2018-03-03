package test;

import java.util.HashMap;
import java.util.Map;

import org.grain.distributedlock.DistributedLockClient;
import org.grain.distributedlock.DistributedlockMsg;
import org.grain.msg.IMsgListener;
import org.grain.msg.MsgPacket;
import org.grain.threadmsg.ThreadMsgManager;

public class TestMsgService implements IMsgListener {

	@Override
	public Map<String, String> getMsgs() throws Exception {
		HashMap<String, String> map = new HashMap<>();
		map.put(DistributedlockMsg.DISTRIBUTEDLOCK_SERVER_CAN_USE, "onDistributedlockCanUse");
		map.put("DISTRIBUTEDLOCK_TEST", "onDistributedlockTest");
		map.put("DISTRIBUTEDLOCK_TEST1", "onDistributedlockTest1");
		return map;
	}

	public void onDistributedlockCanUse(MsgPacket msgPacket) {
		// 发布四条消息，分配至随机线程，不同类型互补影响，相同类型不同键值互不影响
		ThreadMsgManager.dispatchThreadMsg("DISTRIBUTEDLOCK_TEST", null, null);
		ThreadMsgManager.dispatchThreadMsg("DISTRIBUTEDLOCK_TEST", null, null);
		ThreadMsgManager.dispatchThreadMsg("DISTRIBUTEDLOCK_TEST1", null, null);
		ThreadMsgManager.dispatchThreadMsg("DISTRIBUTEDLOCK_TEST1", null, null);
	}

	public void onDistributedlockTest(MsgPacket msgPacket) {
		// 获取锁
		int lockId = DistributedLockClient.getLock("111", "user");
		if (lockId == 0) {
			return;
		}
		/*********** 执行分布式锁业务逻辑 *********/
		System.out.println("分布式锁id为：" + lockId);
		/*********** 执行分布式锁业务逻辑 *********/
		// 释放锁
		DistributedLockClient.unLock("111", "user", lockId);
	}

	public void onDistributedlockTest1(MsgPacket msgPacket) {
		// 获取锁
		int lockId = DistributedLockClient.getLock("222", "user");
		if (lockId == 0) {
			return;
		}
		/*********** 执行分布式锁业务逻辑 *********/
		System.out.println("分布式锁id为：" + lockId);
		/*********** 执行分布式锁业务逻辑 *********/
		// 释放锁
		DistributedLockClient.unLock("222", "user", lockId);
	}

}
