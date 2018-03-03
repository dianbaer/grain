package org.grain.thread;

import java.lang.reflect.Method;

public class ThreadHandle implements IHandle {
	private Object packet;
	private Method method;
	private Object instance;

	/**
	 * 
	 * @param packet
	 *            消息包
	 * @param method
	 *            回调方法
	 * @param instance
	 *            回调实例对象，为null则是说明是静态方法
	 */
	public ThreadHandle(Object packet, Method method, Object instance) {
		this.packet = packet;
		this.method = method;
		this.instance = instance;
	}

	@Override
	public Object getPacket() {
		return packet;
	}

	@Override
	public Method getMethod() {
		return method;
	}

	@Override
	public Object getInstance() {
		return instance;
	}

}
