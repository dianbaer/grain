package org.grain.tcp;

import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;

public class MinaHandler extends IoHandlerAdapter {
	public IoConnector ioConnector;
	public String name;
}
