package thread;

import java.util.ArrayList;

import log.LogManager;

//主线程不掺杂业务逻辑，可以理解为主线程为消息分发线程
public class MainThread extends Thread {
	public int mainThreadCycleInterval;
	private ArrayList<IMainCycle> mainCycleArray = new ArrayList<IMainCycle>();
	private static MainThread instance;

	public MainThread(int mainThreadCycleInterval) {
		this.mainThreadCycleInterval = mainThreadCycleInterval;
	}

	public static void init(int mainThreadCycleInterval) {
		instance = new MainThread(mainThreadCycleInterval);
		instance.setName("MainThread");
	}

	public static MainThread getInstance() {
		return instance;
	}

	public boolean addMainCycle(IMainCycle mainCycle) {
		mainCycleArray.add(mainCycle);
		return true;
	}

	@Override
	public void run() {
		while (true) {
			long startTime = System.currentTimeMillis();
			for (int i = 0; i < mainCycleArray.size(); i++) {
				IMainCycle mainCycle = mainCycleArray.get(i);
				try {
					mainCycle.cycle();
				} catch (Exception e) {
					LogManager.threadLog.error("IMainCycle异常" + mainCycle.getClass().getName(), e);
				}
			}
			long endTime = System.currentTimeMillis();
			if (endTime - startTime < mainThreadCycleInterval) {
				try {
					Thread.sleep(mainThreadCycleInterval - (endTime - startTime));
				} catch (InterruptedException e) {
					LogManager.threadLog.error("主线程睡眠异常", e);
				}
			}
		}
	}
}
