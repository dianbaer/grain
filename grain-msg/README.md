# grain-msg

## grain-msg 通用消息管理工具，可以进行消息注册，消息分发，消息回调


此项目依赖

	grain-log

使用

1、初始化消息管理器，设定打印日志参数
	
	MsgManager.init(true, null);
	
2、将实现IMsgListener接口的类的实例注册到消息管理器

TestMsgListener---实现IMsgListener接口的类

	package org.grain.msg;
	import java.util.HashMap;
	import java.util.Map;
	public class TestMsgListener implements IMsgListener {
		@Override
		public Map<String, String> getMsgs() throws Exception {
			HashMap<String, String> map = new HashMap<>();
			map.put("createuser", "createUserHandle");
			return map;
		}
		public void createUserHandle(MsgPacket msgPacket) {
			System.out.println("接到消息：" + msgPacket.getMsgOpCode());
		}
	}


进行注册

	TestMsgListener testMsgListener = new TestMsgListener();
	boolean result = MsgManager.addMsgListener(testMsgListener);
	
3、通过发布消息，已经注册好监听消息的函数会接到回调

	MsgManager.dispatchMsg("createuser", 111, 222);

	