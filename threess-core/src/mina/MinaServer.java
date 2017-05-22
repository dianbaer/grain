package mina;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class MinaServer {
	private static IoAcceptor ioAcceptor;

	public static void init(String ip, int port, String charset, boolean useTcpMonitor) throws Throwable {
		MinaConfig.CHARSET = Charset.forName(charset);
		MinaConfig.USE_TCP_MONITOR = useTcpMonitor;
		ioAcceptor = new NioSocketAcceptor();
		ioAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MinaEncoder(), new MinaDecoder()));
		MinaServerHandler minaServerHandler = new MinaServerHandler();
		minaServerHandler.ioAcceptor = ioAcceptor;
		ioAcceptor.setHandler(minaServerHandler);

		ioAcceptor.bind(new InetSocketAddress(ip, port));
	}
}
