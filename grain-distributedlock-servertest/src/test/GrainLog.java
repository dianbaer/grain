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
