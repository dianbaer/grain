package org.grain.tcp;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.grain.log.ILog;

public class MinaClient extends Thread {

	private Map<IoConnector, InetSocketAddress> ioConnectorMap = new HashMap<IoConnector, InetSocketAddress>();
	public Map<IoConnector, Boolean> ioConnectorStateMap = new ConcurrentHashMap<IoConnector, Boolean>();
	private static MinaClient instance;
	private static int MINA_CLIENT_RECONNECT_INTERVAL;

	/**
	 * 初始化tcp客户端
	 * 
	 * @param ipArray
	 *            ip地址数组
	 * @param portArray
	 *            端口数组
	 * @param nameArray
	 *            名称数组
	 * @param HandlerClass
	 *            IoHandler接口实现类
	 * @param reconnectInterval
	 *            断线重连间隔 一般设置10单位秒
	 * @param useTcpMonitor
	 *            是否打印监控信息
	 * @param log
	 *            日志可以为null
	 * @throws Exception
	 */
	public static void init(String[] ipArray, int[] portArray, String[] nameArray, Class<?> HandlerClass, int reconnectInterval, boolean useTcpMonitor, ILog log) throws Exception {
		MinaClient.MINA_CLIENT_RECONNECT_INTERVAL = reconnectInterval;
		MinaConfig.USE_TCP_MONITOR = useTcpMonitor;
		MinaConfig.log = log;
		instance = new MinaClient(ipArray, portArray, nameArray, HandlerClass);
	}

	public static MinaClient getInstance() {
		return instance;
	}

	/**
	 * 初始化
	 * 
	 * @param ipArray
	 *            ip地址数组
	 * @param portArray
	 *            端口数组
	 * @param nameArray
	 *            名称数组
	 * @throws Exception
	 */
	public MinaClient(String[] ipArray, int[] portArray, String[] nameArray, Class<?> HandlerClass) throws Exception {
		for (int i = 0; i < ipArray.length; i++) {
			String ip = ipArray[i];
			int port = portArray[i];
			String name = nameArray[i];
			IoConnector ioConnector = new NioSocketConnector();

			ioConnector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MinaEncoder(), new MinaDecoder()));
			MinaHandler minaHandler = (MinaHandler) HandlerClass.newInstance();
			minaHandler.ioConnector = ioConnector;
			minaHandler.name = name;
			ioConnector.setHandler(minaHandler);
			ioConnector.setConnectTimeoutMillis(10000);
			InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, port);
			ioConnectorMap.put(ioConnector, inetSocketAddress);
			ioConnectorStateMap.put(ioConnector, false);
		}
		start();
	}

	/**
	 * 守护，启动链接、断线重连
	 */
	@Override
	public void run() {
		while (true) {
			Set<IoConnector> keySet = ioConnectorMap.keySet();
			Iterator<IoConnector> iterator = keySet.iterator();
			for (int i = 0; i < keySet.size(); i++) {
				IoConnector ioConnector = iterator.next();
				InetSocketAddress inetSocketAddress = ioConnectorMap.get(ioConnector);
				boolean isConnected = ioConnectorStateMap.get(ioConnector);
				if (!isConnected) {
					ConnectFuture connectFuture = ioConnector.connect(inetSocketAddress);
					connectFuture.awaitUninterruptibly();
					if (!connectFuture.isConnected()) {
						connectFuture.cancel();
						if (MinaConfig.log != null) {
							MinaConfig.log.info("连接" + inetSocketAddress.toString() + "失败");
						}
					} else {
						ioConnectorStateMap.put(ioConnector, true);
						if (MinaConfig.log != null) {
							MinaConfig.log.info("连接" + inetSocketAddress.toString() + "成功");
						}
					}
				}
			}
			try {
				Thread.sleep(MinaClient.MINA_CLIENT_RECONNECT_INTERVAL);
			} catch (InterruptedException e) {
				if (MinaConfig.log != null) {
					MinaConfig.log.error("守护线程minaclient异常", e);
				}
			}
		}
	}

}
