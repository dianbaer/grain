package mina;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import log.LogManager;
import net.sf.json.JSONArray;

public class MinaClient extends Thread {

	private Map<IoConnector, InetSocketAddress> ioConnectorMap = new HashMap<IoConnector, InetSocketAddress>();
	public Map<IoConnector, Boolean> ioConnectorStateMap = new ConcurrentHashMap<IoConnector, Boolean>();
	private static MinaClient instance;

	public static void init(JSONArray ipArray, JSONArray portArray, String nameClass, String charset, int reconnectInterval, boolean useTcpMonitor) throws Exception {
		MinaConfig.CHARSET = Charset.forName(charset);
		MinaConfig.MINA_CLIENT_RECONNECT_INTERVAL = reconnectInterval;
		MinaConfig.USE_TCP_MONITOR = useTcpMonitor;
		instance = new MinaClient(ipArray, portArray, nameClass);
	}

	public static MinaClient getInstance() {
		return instance;
	}

	public MinaClient(JSONArray ipArray, JSONArray portArray, String nameClass) throws Exception {
		IMinaClientName minaClientName = (IMinaClientName) Class.forName(nameClass).newInstance();
		for (int i = 0; i < ipArray.size(); i++) {
			String ip = ipArray.getString(i);
			int port = portArray.getInt(i);
			String name = minaClientName.getClientNames()[i];
			IoConnector ioConnector = new NioSocketConnector();

			ioConnector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MinaEncoder(), new MinaDecoder()));
			MinaClientHandler minaClientHandler = new MinaClientHandler();
			minaClientHandler.ioConnector = ioConnector;
			minaClientHandler.name = name;
			ioConnector.setHandler(minaClientHandler);
			ioConnector.setConnectTimeoutMillis(10000);
			InetSocketAddress inetSocketAddress = new InetSocketAddress(ip, port);
			ioConnectorMap.put(ioConnector, inetSocketAddress);
			ioConnectorStateMap.put(ioConnector, false);
		}
		start();
	}

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
						// LogManager.minaLog.info("连接" +
						// inetSocketAddress.toString() + "失败");
					} else {
						ioConnectorStateMap.put(ioConnector, true);
						LogManager.minaLog.info("连接" + inetSocketAddress.toString() + "成功");
					}
				}
			}
			try {
				Thread.sleep(MinaConfig.MINA_CLIENT_RECONNECT_INTERVAL);
			} catch (InterruptedException e) {
				LogManager.threadLog.error("守护线程minaclient异常", e);
			}
		}
	}

}
