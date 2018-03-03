# grain-log

## grain-log 日志接口，所有组件想打印日志必须关联这个库


此项目依赖

	无

使用

1、实现Ilog接口

GrainLog-----------实现ILog接口，把可以打印日志的类的对象传递进来，例如org.slf4j.Logger

	package test;
	import org.grain.log.ILog;
	import org.slf4j.Logger;
	public class GrainLog implements ILog {
		private Logger log;
		public GrainLog(Logger log) {
			this.log = log;
		}
		@Override
		public void warn(String warn) {
			this.log.warn(warn);
		}
		@Override
		public void error(String error, Throwable e) {
			this.log.error(error, e);
		}
		@Override
		public void info(String info) {
			this.log.info(info);
		}
	}

	
2、将实现ILog接口的实例传递给依赖此接口的组件。例如：grain-msg项目

	GrainLog grainLog1 = new GrainLog(LoggerFactory.getLogger("msgLog"));
	// 初始化消息
	MsgManager.init(true, grainLog1);
	