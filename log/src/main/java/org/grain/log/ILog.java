package org.grain.log;

public interface ILog {
	/**
	 * 警告，用于未抛异常，但是业务错误
	 * 
	 * @param warn
	 *            警告字符串
	 */
	public void warn(String warn);

	/**
	 * 抛异常
	 * 
	 * @param error
	 *            错误字符串
	 * @param e
	 *            错误的异常信息
	 */
	public void error(String error, Throwable e);

	/**
	 * 普通日志
	 * 
	 * @param info
	 *            普通日志字符串
	 */
	public void info(String info);
}
