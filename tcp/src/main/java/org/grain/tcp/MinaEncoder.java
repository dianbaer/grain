
package org.grain.tcp;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class MinaEncoder extends ProtocolEncoderAdapter {
	public static final byte[] HEAD = { 'U', 'A' };

	@Override
	public void encode(IoSession session, Object obj, ProtocolEncoderOutput out) throws Exception {
		TcpPacket packet = (TcpPacket) obj;
		byte[] byteData = packet.getByteData();
		int len = 18 + byteData.length;
		IoBuffer buf = IoBuffer.allocate(len);
		buf.put(HEAD);
		buf.putInt(len);
		buf.putInt(packet.gettOpCode());
		buf.putInt(packet.lockedId);
		buf.putInt(packet.unlockedId);
		buf.put(byteData);
		buf.flip();
		out.write(buf);

	}

}
