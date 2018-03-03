package org.grain.config;

public interface ITemplate {
	/**
	 * 模板类型数据id
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * 初始化模板数据函数
	 * 
	 * @param data
	 *            字符串数组
	 * @return ITemplate初始化完成的模板
	 * @throws Exception
	 */
	public ITemplate initTemplate(String[] data) throws Exception;
}
