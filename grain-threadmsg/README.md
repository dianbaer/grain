# grain-threadmsg

## grain-threadmsg 通用消息与通用线程整合，可以进行异步线程消息分发处理


此项目依赖

	grain-log
	grain-thread
	grain-msg

使用

1、初始化线程和消息

	AsyncThreadManager.init(100, 10, 3, 0, null);
	AsyncThreadManager.start();
	MsgManager.init(true, null);
	
2、初始化操作码归属线程优先级，如果不初始化则随机选线程和优先级

	ThreadMsgManager.addMapping("createuser", new int[] { 1, 1 });
	
3、实现IMsgListener接口并放入监听

TestThreadMsgListener---实现IMsgListener的类

	package org.grain.threadmsg;
	import java.util.HashMap;
	import java.util.Map;
	import org.grain.msg.IMsgListener;
	import org.grain.msg.MsgPacket;
	public class TestThreadMsgListener implements IMsgListener {
		@Override
		public Map<String, String> getMsgs() throws Exception {
			HashMap<String, String> map = new HashMap<>();
			map.put("createuser", "createUserHandle");
			map.put("updateuser", "updateUserHandle");
			return map;
		}
		public void createUserHandle(MsgPacket msgPacket) {
			System.out.println("createUserHandle接到消息：" + msgPacket.getMsgOpCode());
		}
		public void updateUserHandle(MsgPacket msgPacket) {
			System.out.println("updateUserHandle接到消息：" + msgPacket.getMsgOpCode());
		}
	}


放入监听，关注消息

	TestThreadMsgListener testThreadMsgListener = new TestThreadMsgListener();
	MsgManager.addMsgListener(testThreadMsgListener);
	
4、发布消息，进行关注消息推送至异步线程，异步线程进行关注消息函数回调

	ThreadMsgManager.dispatchThreadMsg("createuser", 111, 222);
	ThreadMsgManager.dispatchThreadMsg("updateuser", 111, 222);

	