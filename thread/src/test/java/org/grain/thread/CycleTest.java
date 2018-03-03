package org.grain.thread;

public class CycleTest implements ICycle {
	public String name;

	@Override
	public void cycle() throws Exception {
		System.out.println(name + "业务轮训，线程：" + Thread.currentThread().getName());
	}

	@Override
	public void onAdd() throws Exception {
		System.out.println(name + "加入动作，线程：" + Thread.currentThread().getName());
	}

	@Override
	public void onRemove() throws Exception {
		System.out.println(name + "离开动作，线程：" + Thread.currentThread().getName());
	}

}
