package thread;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import msg.MsgPacket;
import tcp.TcpPacket;
import ws.WsPacket;

public class AsyncHandleData {
	public LinkedBlockingQueue<TcpPacket> tcpPool = new LinkedBlockingQueue<TcpPacket>();
	private ArrayList<TcpPacket> handleTcpPool = new ArrayList<TcpPacket>();
	public LinkedBlockingQueue<WsPacket> wsPool = new LinkedBlockingQueue<WsPacket>();
	private ArrayList<WsPacket> handleWSPool = new ArrayList<WsPacket>();
	public LinkedBlockingQueue<MsgPacket> msgPool = new LinkedBlockingQueue<MsgPacket>();
	private ArrayList<MsgPacket> handleMsgPool = new ArrayList<MsgPacket>();

	public ArrayList<ICycle> initCycleArray = new ArrayList<ICycle>();

	// 动态增减的轮训
	public ArrayList<IChangeCycle> changeCycleArray = new ArrayList<IChangeCycle>();
	public LinkedBlockingQueue<IChangeCycle> addChangeCycleQueue = new LinkedBlockingQueue<IChangeCycle>();
	private ArrayList<IChangeCycle> addChangeCycleArray = new ArrayList<IChangeCycle>();
	public LinkedBlockingQueue<IChangeCycle> removeChangeCycleQueue = new LinkedBlockingQueue<IChangeCycle>();
	private ArrayList<IChangeCycle> removeChangeCycleArray = new ArrayList<IChangeCycle>();

	public ArrayList<TcpPacket> getHandleTcpPool() {
		handleTcpPool.clear();
		tcpPool.drainTo(handleTcpPool);
		return handleTcpPool;
	}

	public ArrayList<WsPacket> getHandleWSPool() {
		handleWSPool.clear();
		wsPool.drainTo(handleWSPool);
		return handleWSPool;
	}

	public ArrayList<MsgPacket> getHandleMsgPool() {
		handleMsgPool.clear();
		msgPool.drainTo(handleMsgPool);
		return handleMsgPool;
	}

	public ArrayList<IChangeCycle> getAddChangeCycle() {
		addChangeCycleArray.clear();
		addChangeCycleQueue.drainTo(addChangeCycleArray);
		return addChangeCycleArray;
	}

	public ArrayList<IChangeCycle> getRemoveChangeCycle() {
		removeChangeCycleArray.clear();
		removeChangeCycleQueue.drainTo(removeChangeCycleArray);
		return removeChangeCycleArray;
	}

}
