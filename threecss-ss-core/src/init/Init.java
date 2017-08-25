package init;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import config.CommonConfig;
import config.ConfigManager;
import config.DefaultConfig;
import http.HttpFilterManager;
import http.HttpManager;
import http.HttpUtil;
import http.IHttpListener;
import http.gateway.GateWayManager;
import keylock.KeyLockManager;
import log.LogManager;
import mbatis.MybatisManager;
import mina.ITcpListener;
import mina.MinaClient;
import mina.MinaClientService;
import mina.MinaServer;
import mina.MinaServerService;
import mina.TcpManager;
import mina.distributedlock.DistributedLockClient;
import mina.distributedlock.DistributedLockServer;
import mina.distributedlock.DistributedLockService;
import mina.waitlock.WaitLockManager;
import msg.IMsgListener;
import msg.MsgManager;
import msg.MsgOpCode;
import redis.RedisManager;
import service.IService;
import tcp.TOpCode;
import thread.AsyncThreadManager;
import websocket.IWSListener;
import websocket.WSManager;
import websocket.WebSocketServerService;

public class Init {
	private static HashMap<Class<?>, Object> initServiceMap = new HashMap<>();

	public static void init(String configFileName) throws Throwable {
		Properties properties = loadConfig(configFileName);
		ConfigManager.init(properties.getProperty("config_dir"), properties.getProperty("config_name"));
		if (CommonConfig.REDIS_USE) {
			RedisManager.init(CommonConfig.REDIS_IP, CommonConfig.REDIS_PORT);
		}
		if (CommonConfig.MARIADB_USE) {
			MybatisManager.init(properties.getProperty("config_dir"));
		}
		MsgManager.init(CommonConfig.USE_MSG_MONITOR);
		MsgOpCode.init();
		if (CommonConfig.IS_HTTP_SERVER) {
			HttpFilterManager.init(CommonConfig.HTTP_FILTER_ARRAY, DefaultConfig.ISO_8859_1, DefaultConfig.UTF8, CommonConfig.UPLOAD_TEMP_FOLDER, CommonConfig.UPLOAD_PROGRESS_CLASS, CommonConfig.DOWNLOAD_BLOCK_SIZE, CommonConfig.DOWNLOAD_FILE_SLEEP_TIME, CommonConfig.DOWNLOAD_IMAGE_SLEEP_TIME, CommonConfig.DOWNLOAD_OTHER_STREAM_SLEEP_TIME, CommonConfig.USE_HTTP_MONITOR, CommonConfig.IS_GATE_WAY_SERVER);
			if (CommonConfig.IS_GATE_WAY_SERVER) {
				GateWayManager.init(CommonConfig.GATE_WAY_CONFIG);
			}
		}
		HttpUtil.init(DefaultConfig.ISO_8859_1, DefaultConfig.UTF8);
		KeyLockManager.init(CommonConfig.KEY_LOCK_TYPE_CLASS, CommonConfig.KEY_LOCK_EXPIRE_TIME, CommonConfig.KEY_LOCK_CYCLE_SLEEP_TIME);
		IExpand expand = (IExpand) Class.forName(CommonConfig.EXPAND_CLASS).newInstance();
		expand.init();

		AsyncThreadManager.init(CommonConfig.ASYNC_THREAD_CYCLE_INTERVAL, CommonConfig.ASYNC_THREAD_NUM, CommonConfig.ASYNC_THREAD_PRIORITY_NUM);

		if (CommonConfig.IS_MINA_CLIENT || CommonConfig.IS_MINA_SERVER) {

			WaitLockManager.init(CommonConfig.WAIT_LOCK_TIME);
			TOpCode.init();
		}
		if (CommonConfig.IS_MINA_SERVER) {
			registerService(MinaServerService.class);
			if (CommonConfig.IS_DISTRIBUTED_LOCK_SERVER) {
				DistributedLockServer.init(CommonConfig.DISTRIBUTED_LOCK_SERVER_CLASS);
				registerService(DistributedLockService.class);
			}
			MinaServer.init(CommonConfig.MINA_SERVER_IP, CommonConfig.MINA_SERVER_PORT, CommonConfig.UTF8, CommonConfig.USE_TCP_MONITOR);
		}

		if (CommonConfig.IS_MINA_CLIENT) {
			registerService(MinaClientService.class);
			if (CommonConfig.IS_DISTRIBUTED_LOCK_CLIENT) {
				DistributedLockClient.init(CommonConfig.DISTRIBUTED_LOCK_CLIENT_CLASS);
			}
			MinaClient.init(CommonConfig.CONNECT_MINA_SERVER_IP, CommonConfig.CONNECT_MINA_SERVER_PORT, CommonConfig.CONNECT_MINA_SERVER_NAME, CommonConfig.UTF8, CommonConfig.MINA_CLIENT_RECONNECT_INTERVAL, CommonConfig.USE_TCP_MONITOR);
		}
		registerService(WebSocketServerService.class);

		expand.threadInit();

		AsyncThreadManager.start();
	}

	private static Properties loadConfig(String configFileName) throws Throwable {
		LogManager.initLog.info("初始化基础配置文件");
		InputStream inputStream = null;
		URL url = Init.class.getClassLoader().getResource(configFileName);
		if (url != null) {
			LogManager.initLog.info("Init.class.getClassLoader().getResource找到配置文件，路径为：" + url.getPath());
			inputStream = Init.class.getClassLoader().getResourceAsStream(configFileName);
		} else {
			LogManager.initLog.info("Init.class.getClassLoader().getResource：" + Init.class.getClassLoader().getResource("").getPath() + "，未找到配置文件：" + configFileName);
		}
		if (inputStream == null) {
			File file = new File(System.getProperty("catalina.base") + "/" + configFileName);
			if (file.exists()) {
				LogManager.initLog.info("System.getProperty(\"catalina.base\")找到配置文件，路径为" + System.getProperty("catalina.base") + "/" + configFileName);
				inputStream = new FileInputStream(file);
			} else {
				LogManager.initLog.info("System.getProperty(\"catalina.base\")：" + System.getProperty("catalina.base") + "，未找到配置文件：" + configFileName);
			}
		}
		if (inputStream == null) {
			File file = new File(configFileName);
			if (file.exists()) {
				LogManager.initLog.info("找到配置文件，路径为" + file.getAbsolutePath());
				inputStream = new FileInputStream(file);
			} else {
				LogManager.initLog.info("未找到配置文件：" + configFileName);
			}
		}
		if (inputStream != null) {
			Properties properties = new Properties();
			properties.load(inputStream);
			LogManager.initLog.info("初始化基础配置文件完成");
			inputStream.close();
			return properties;
		} else {
			LogManager.initLog.warn("未找到配置文件：" + configFileName);
			throw new Exception("未找到配置文件" + configFileName);
		}
	}

	public static void registerService(Class<?> className) throws Exception {
		Object service = className.newInstance();
		if (service instanceof ITcpListener) {
			TcpManager.addTcpListener((ITcpListener) service);
		}
		if (service instanceof IWSListener) {
			WSManager.addWSListener((IWSListener) service);
		}
		if (service instanceof IMsgListener) {
			MsgManager.addMsgListener((IMsgListener) service);
		}
		if (service instanceof IHttpListener) {
			HttpManager.addHttpListener((IHttpListener) service);
		}
		if (service instanceof IService) {
			((IService) service).init();
		}
		initServiceMap.put(className, service);
	}

	public static Object getService(Class<?> className) {
		return initServiceMap.get(className);
	}
}
