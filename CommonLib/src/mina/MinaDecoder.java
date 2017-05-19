package mina;

import java.lang.reflect.Method;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.google.protobuf.Message;

import log.LogManager;
import tcp.TOpCode;
import tcp.TcpPacket;

public class MinaDecoder extends ProtocolDecoderAdapter {

	private static final String BUFFER = ".Buffer";
	private static final int U = 85;
	private static final int A = 65;

	@Override
	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		IoBuffer buf = (IoBuffer) session.getAttribute(BUFFER);
		if (buf != null) {
			buf.put(in);
			buf.flip();
		} else {
			buf = in;
		}
		for (;;) {
			if (buf.remaining() > 6) {
				int pos = buf.position();

				int head1 = buf.get();
				int head2 = buf.get();
				if (head1 == U && head2 == A) {
					int len = buf.getInt();
					if (buf.remaining() >= (len - 6)) {
						int tOpCode = buf.getInt();
						int lockedId = buf.getInt();
						int unlockedId = buf.getInt();
						byte[] bytes = new byte[len - 18];
						buf.get(bytes);
						Class<?> className = TOpCode.tOpCodeMap.get(tOpCode);
						Message data = null;
						Method m = className.getDeclaredMethod("parseFrom", new Class[] { byte[].class });
						data = (Message) m.invoke(null, bytes);
						TcpPacket packet = new TcpPacket(tOpCode, data, MinaConfig.USE_TCP_MONITOR);
						packet.lockedId = lockedId;
						packet.unlockedId = unlockedId;
						packet.session = session;
						out.write(packet);
					} else {
						buf.position(pos);
						break;
					}
				} else {

					session.setAttribute(BUFFER, null);
					LogManager.minaLog.error("头消息错误，客户端或服务器不合法");
					throw new Exception("头消息错误，客户端或服务器不合法");
				}

			} else {
				break;
			}
		}
		if (buf.hasRemaining()) {
			storeRemainingInSession(buf, session);
		} else {
			session.setAttribute(BUFFER, null);
		}
	}

	private void storeRemainingInSession(IoBuffer buf, IoSession session) {
		IoBuffer remainingBuf = IoBuffer.allocate(buf.capacity());
		remainingBuf.setAutoExpand(true);
		remainingBuf.order(buf.order());
		remainingBuf.put(buf);
		session.setAttribute(BUFFER, remainingBuf);
	}

}
