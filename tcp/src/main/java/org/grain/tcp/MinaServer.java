package org.grain.tcp;

import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.grain.log.ILog;

public class MinaServer {
	private static IoAcceptor ioAcceptor;

	/**
	 * 初始化tcp服务器
	 * 
	 * @param ip
	 *            监听ip 0.0.0.0最好
	 * @param port
	 *            监听端口
	 * @param HandlerClass
	 *            继承MinaHandler的类
	 * @param useTcpMonitor
	 *            是否输出监控日志
	 * @param log
	 *            日志可以为null
	 * @throws Throwable
	 */
	public static void init(String ip, int port, Class<?> HandlerClass, boolean useTcpMonitor, ILog log) throws Exception {
		MinaConfig.USE_TCP_MONITOR = useTcpMonitor;
		MinaConfig.log = log;
		// 初始化
		ioAcceptor = new NioSocketAcceptor();
		// 设置编码解码
		ioAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MinaEncoder(), new MinaDecoder()));
		// 设置处理函数实现IoHandler接口
		MinaHandler minaHandler = (MinaHandler) HandlerClass.newInstance();
		ioAcceptor.setHandler(minaHandler);
		// 绑定ip地址与端口
		ioAcceptor.bind(new InetSocketAddress(ip, port));
	}
}
