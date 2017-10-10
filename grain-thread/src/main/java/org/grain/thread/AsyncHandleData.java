package org.grain.thread;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class AsyncHandleData {
	/**
	 * 等待的处理队列
	 */
	public LinkedBlockingQueue<IHandle> waitHandleQueue = new LinkedBlockingQueue<IHandle>();
	/**
	 * 帮助类，减少垃圾回收
	 */
	private ArrayList<IHandle> handleArray = new ArrayList<IHandle>();
	/**
	 * 轮训队列
	 */
	public ArrayList<ICycle> cycleArray = new ArrayList<ICycle>();
	/**
	 * 等待加入的轮训队列
	 */
	public LinkedBlockingQueue<ICycle> waitAddCycleQueue = new LinkedBlockingQueue<ICycle>();
	/**
	 * 帮助类，减少垃圾回收
	 */
	private ArrayList<ICycle> addCycleArray = new ArrayList<ICycle>();
	/**
	 * 等待移除的轮训队列
	 */
	public LinkedBlockingQueue<ICycle> waitRemoveCycleQueue = new LinkedBlockingQueue<ICycle>();
	/**
	 * 帮助类，减少垃圾回收
	 */
	private ArrayList<ICycle> removeCycleArray = new ArrayList<ICycle>();

	/**
	 * 获取本次轮训处理的数组
	 * 
	 * @return
	 */
	public ArrayList<IHandle> getHandleArray() {
		handleArray.clear();
		waitHandleQueue.drainTo(handleArray);
		return handleArray;
	}

	/**
	 * 获取本次轮训处理的加入轮训
	 * 
	 * @return
	 */
	public ArrayList<ICycle> getAddCycleArray() {
		addCycleArray.clear();
		waitAddCycleQueue.drainTo(addCycleArray);
		return addCycleArray;
	}

	/**
	 * 获取本次轮训处理的移除轮训
	 * 
	 * @return
	 */
	public ArrayList<ICycle> getRemoveCycleArray() {
		removeCycleArray.clear();
		waitRemoveCycleQueue.drainTo(removeCycleArray);
		return removeCycleArray;
	}

}
