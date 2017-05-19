package config;

import config.ConfigManager.JsonConfigType;
import log.LogManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CommonConfig extends DefaultConfig {
	public static boolean REDIS_USE;
	public static String REDIS_IP;
	public static int REDIS_PORT;
	public static boolean MARIADB_USE;
	public static boolean IS_MINA_SERVER;
	public static String MINA_SERVER_IP;
	public static int MINA_SERVER_PORT;
	public static boolean IS_MINA_CLIENT;
	public static JSONArray CONNECT_MINA_SERVER_IP;
	public static JSONArray CONNECT_MINA_SERVER_PORT;
	public static String CONNECT_MINA_SERVER_NAME;
	public static int MINA_CLIENT_RECONNECT_INTERVAL;
	public static String EXPAND_CLASS;
	public static int MAIN_THREAD_CYCLE_INTERVAL;
	public static int ASYNC_THREAD_CYCLE_INTERVAL;
	public static int ASYNC_THREAD_NUM;
	public static int ASYNC_THREAD_PRIORITY_NUM;
	public static int WAIT_LOCK_TIME;
	public static boolean IS_HTTP_SERVER;
	public static JSONArray HTTP_FILTER_ARRAY;
	public static String UPLOAD_TEMP_FOLDER;
	public static String UPLOAD_PROGRESS_CLASS;
	public static int DOWNLOAD_BLOCK_SIZE;
	public static int DOWNLOAD_FILE_SLEEP_TIME;
	public static int DOWNLOAD_IMAGE_SLEEP_TIME;
	public static boolean IS_USE_TOMCAT_THREAD_HANDLE;
	public static boolean IS_GATE_WAY_SERVER;
	public static String GATE_WAY_CONFIG;
	public static String KEY_LOCK_TYPE_CLASS;
	public static int KEY_LOCK_EXPIRE_TIME;
	public static int KEY_LOCK_CYCLE_SLEEP_TIME;
	public static boolean USE_HTTP_MONITOR;
	public static boolean USE_TCP_MONITOR;
	public static boolean USE_MSG_MONITOR;
	public static boolean IS_DISTRIBUTED_LOCK_SERVER;
	public static String DISTRIBUTED_LOCK_SERVER_CLASS;
	public static boolean IS_DISTRIBUTED_LOCK_CLIENT;
	public static String DISTRIBUTED_LOCK_CLIENT_CLASS;

	public static void init() {
		LogManager.initLog.info("初始化CommonConfig");
		JSONObject config = ConfigManager.getJsonData(JsonConfigType.CONFIG.getTypeValue());
		// redis
		REDIS_USE = config.getJSONArray("redisUse").getBoolean(0);
		REDIS_IP = config.getJSONArray("redisIP").getString(0);
		REDIS_PORT = config.getJSONArray("redisPort").getInt(0);
		// mariadb
		MARIADB_USE = config.getJSONArray("mariadbUse").getBoolean(0);
		// minaserver
		IS_MINA_SERVER = config.getJSONArray("isMinaServer").getBoolean(0);
		MINA_SERVER_IP = config.getJSONArray("minaServerIP").getString(0);
		MINA_SERVER_PORT = config.getJSONArray("minaServerPort").getInt(0);
		// minaclient
		IS_MINA_CLIENT = config.getJSONArray("isMinaClient").getBoolean(0);
		CONNECT_MINA_SERVER_IP = config.getJSONArray("connectMinaServerIP");
		CONNECT_MINA_SERVER_PORT = config.getJSONArray("connectMinaServerPort");
		CONNECT_MINA_SERVER_NAME = config.getJSONArray("connectMinaServerName").getString(0);
		MINA_CLIENT_RECONNECT_INTERVAL = config.getJSONArray("minaClientReConnectInterval").getInt(0);
		// 扩展启动类
		EXPAND_CLASS = config.getJSONArray("expandClass").getString(0);
		// 线程配置
		MAIN_THREAD_CYCLE_INTERVAL = config.getJSONArray("mainThreadCycleInterval").getInt(0);
		ASYNC_THREAD_CYCLE_INTERVAL = config.getJSONArray("asyncThreadCycleInterval").getInt(0);
		ASYNC_THREAD_NUM = config.getJSONArray("asyncThreadNum").getInt(0);
		ASYNC_THREAD_PRIORITY_NUM = config.getJSONArray("asyncThreadPriorityNum").getInt(0);
		// 等待锁
		WAIT_LOCK_TIME = config.getJSONArray("waitLockTime").getInt(0);
		// http
		IS_HTTP_SERVER = config.getJSONArray("isHttpServer").getBoolean(0);
		HTTP_FILTER_ARRAY = config.getJSONArray("httpFilterArray");
		UPLOAD_TEMP_FOLDER = config.getJSONArray("uploadTempFolder").getString(0);
		UPLOAD_PROGRESS_CLASS = config.getJSONArray("uploadProgressClass").getString(0);
		DOWNLOAD_BLOCK_SIZE = config.getJSONArray("downloadBlockSize").getInt(0);
		DOWNLOAD_FILE_SLEEP_TIME = config.getJSONArray("downloadFileSleepTime").getInt(0);
		DOWNLOAD_IMAGE_SLEEP_TIME = config.getJSONArray("downloadImageSleepTime").getInt(0);
		IS_USE_TOMCAT_THREAD_HANDLE = config.getJSONArray("isUseTomcatThreadHandle").getBoolean(0);
		IS_GATE_WAY_SERVER = config.getJSONArray("isGateWayServer").getBoolean(0);
		GATE_WAY_CONFIG = config.getJSONArray("gateWayConfig").getString(0);
		// key锁
		KEY_LOCK_TYPE_CLASS = config.getJSONArray("keyLockTypeClass").getString(0);
		KEY_LOCK_EXPIRE_TIME = config.getJSONArray("keyLockExpireTime").getInt(0);
		KEY_LOCK_CYCLE_SLEEP_TIME = config.getJSONArray("keyLockCycleSleepTime").getInt(0);
		// monitor
		USE_HTTP_MONITOR = config.getJSONArray("useHttpMonitor").getBoolean(0);
		USE_TCP_MONITOR = config.getJSONArray("useTcpMonitor").getBoolean(0);
		USE_MSG_MONITOR = config.getJSONArray("useMsgMonitor").getBoolean(0);
		// 分布式锁
		IS_DISTRIBUTED_LOCK_SERVER = config.getJSONArray("isDistributedLockServer").getBoolean(0);
		DISTRIBUTED_LOCK_SERVER_CLASS = config.getJSONArray("distributedLockServerClass").getString(0);
		IS_DISTRIBUTED_LOCK_CLIENT = config.getJSONArray("isDistributedLockClient").getBoolean(0);
		DISTRIBUTED_LOCK_CLIENT_CLASS = config.getJSONArray("distributedLockClientClass").getString(0);
		LogManager.initLog.info("初始化CommonConfig完成");
	}
}
