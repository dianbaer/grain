package log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogManager {
	public static Logger initLog = LoggerFactory.getLogger("initLog");
	public static Logger cycleLog = LoggerFactory.getLogger("cycleLog");
	public static Logger httpLog = LoggerFactory.getLogger("httpLog");
	public static Logger redisLog = LoggerFactory.getLogger("redisLog");
	public static Logger mariadbLog = LoggerFactory.getLogger("mariadbLog");
	public static Logger minaLog = LoggerFactory.getLogger("minaLog");
	public static Logger msgLog = LoggerFactory.getLogger("msgLog");
	public static Logger threadLog = LoggerFactory.getLogger("threadLog");
	public static Logger keylockLog = LoggerFactory.getLogger("keylockLog");
	public static Logger tcpmonitorLog = LoggerFactory.getLogger("tcpmonitorLog");
	public static Logger httpmonitorLog = LoggerFactory.getLogger("httpmonitorLog");
	public static Logger msgmonitorLog = LoggerFactory.getLogger("msgmonitorLog");
	public static Logger wsmonitorLog = LoggerFactory.getLogger("wsmonitorLog");
	public static Logger distributedlockLog = LoggerFactory.getLogger("distributedlockLog");
	public static Logger websocketLog = LoggerFactory.getLogger("websocketLog");
	public static Logger mongodbLog = LoggerFactory.getLogger("mongodbLog");

}
