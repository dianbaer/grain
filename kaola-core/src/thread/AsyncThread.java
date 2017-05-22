package thread;

import java.util.ArrayList;
import java.util.HashMap;

import log.LogManager;
import mina.TcpManager;
import msg.MsgManager;
import msg.MsgPacket;
import tcp.TcpPacket;
import websocket.WSManager;
import ws.WsPacket;

public class AsyncThread extends Thread {
	public int asyncThreadCycleInterval;
	public HashMap<Integer, AsyncHandleData> asyncHandleDataMap = new HashMap<Integer, AsyncHandleData>();

	public AsyncThread(int asyncThreadCycleInterval, String name) {
		this.asyncThreadCycleInterval = asyncThreadCycleInterval;
		this.setName(name);
	}

	@Override
	public void run() {
		while (true) {
			long startTime = System.currentTimeMillis();
			Object[] priorityArray = asyncHandleDataMap.keySet().toArray();
			for (int i = 0; i < asyncHandleDataMap.size(); i++) {
				AsyncHandleData asyncHandleData = asyncHandleDataMap.get(priorityArray[i]);
				ArrayList<IChangeCycle> addChangeCycleArray = asyncHandleData.getAddChangeCycle();
				ArrayList<IChangeCycle> removeChangeCycleArray = asyncHandleData.getRemoveChangeCycle();

				for (int j = 0; j < addChangeCycleArray.size(); j++) {
					IChangeCycle changeCycle = addChangeCycleArray.get(j);
					asyncHandleData.changeCycleArray.add(changeCycle);
					try {
						changeCycle.onAdd();
					} catch (Exception e) {
						LogManager.threadLog.error("异步线程异常IChangeCycle onAdd:" + changeCycle.getClass().getName(), e);
					}

				}
				for (int j = 0; j < removeChangeCycleArray.size(); j++) {
					IChangeCycle changeCycle = removeChangeCycleArray.get(j);
					asyncHandleData.changeCycleArray.remove(changeCycle);
					try {
						changeCycle.onRemove();
					} catch (Exception e) {
						LogManager.threadLog.error("异步线程异常IChangeCycle onRemove:" + changeCycle.getClass().getName(), e);
					}
				}
				for (int j = 0; j < asyncHandleData.changeCycleArray.size(); j++) {
					IChangeCycle changeCycle = asyncHandleData.changeCycleArray.get(j);
					try {
						changeCycle.cycle();
					} catch (Exception e) {
						LogManager.threadLog.error("异步线程异常IChangeCycle cycle:" + changeCycle.getClass().getName(), e);
					}
				}

				for (int n = 0; n < asyncHandleData.initCycleArray.size(); n++) {
					ICycle cycle = asyncHandleData.initCycleArray.get(n);
					try {
						cycle.cycle();
					} catch (Exception e) {
						LogManager.threadLog.error("异步线程异常ICycle cycle:" + cycle.getClass().getName(), e);
					}
				}

				// 先取tcp的，后取msg的
				ArrayList<TcpPacket> tcpArray = asyncHandleData.getHandleTcpPool();
				ArrayList<WsPacket> wsArray = asyncHandleData.getHandleWSPool();
				ArrayList<MsgPacket> msgArray = asyncHandleData.getHandleMsgPool();

				// 先处理msg的，后处理tcp的，最后处理cycle服务
				for (int j = 0; j < msgArray.size(); j++) {
					MsgPacket msgPacket = msgArray.get(j);
					try {
						MsgManager.handleMsg(msgPacket);
					} catch (Exception e) {
						LogManager.threadLog.error("异步线程异常MsgPacket:" + msgPacket.getMsgOpCode(), e);
					}
				}
				for (int m = 0; m < tcpArray.size(); m++) {
					TcpPacket tcpPacket = tcpArray.get(m);
					try {
						TcpManager.handleTcp(tcpPacket);
					} catch (Exception e) {
						LogManager.threadLog.error("异步线程异常TcpPacket:" + tcpPacket.gettOpCode(), e);
					}
				}
				for (int m = 0; m < wsArray.size(); m++) {
					WsPacket wsPacket = wsArray.get(m);
					try {
						WSManager.handleWS(wsPacket);
					} catch (Exception e) {
						LogManager.threadLog.error("异步线程异常WsPacket:" + wsPacket.getWsOpCode(), e);
					}
				}

			}
			long endTime = System.currentTimeMillis();
			if (endTime - startTime < asyncThreadCycleInterval) {
				try {
					Thread.sleep(asyncThreadCycleInterval - (endTime - startTime));
				} catch (InterruptedException e) {
					LogManager.threadLog.error("异步线程睡眠异常", e);
				}
			}
		}
	}

}
