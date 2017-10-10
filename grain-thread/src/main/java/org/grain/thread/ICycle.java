package org.grain.thread;

public interface ICycle {
	/**
	 * 每次轮训的业务
	 * 
	 * @throws Exception
	 */
	public void cycle() throws Exception;

	/**
	 * 加入动作的业务
	 * 
	 * @throws Exception
	 */
	public void onAdd() throws Exception;

	/**
	 * 移除动作的业务
	 * 
	 * @throws Exception
	 */
	public void onRemove() throws Exception;
}
