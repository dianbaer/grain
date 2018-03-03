package org.grain.redis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.grain.log.ILog;

public class SerializingConverter implements IConverter<Object, byte[]> {
	private ILog log;

	public SerializingConverter(ILog log) {
		this.log = log;
	}

	@Override
	public byte[] convert(Object source) {
		ByteArrayOutputStream byteArrayOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			if (!(source instanceof Serializable)) {
				throw new IllegalArgumentException("该对象未实现序列化接口");
			}
			byteArrayOutputStream = new ByteArrayOutputStream(256);
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(source);
			objectOutputStream.flush();
			return byteArrayOutputStream.toByteArray();
		} catch (Exception e) {
			if (log != null) {
				log.error("redis序列化异常", e);
			}
			return null;
		} finally {
			if (byteArrayOutputStream != null) {
				try {
					byteArrayOutputStream.close();
				} catch (IOException e) {
					if (log != null) {
						log.error("redi关闭流异常", e);
					}
				}
			}
			if (objectOutputStream != null) {
				try {
					objectOutputStream.close();
				} catch (IOException e) {
					if (log != null) {
						log.error("redi关闭流异常", e);
					}
				}
			}
		}

	}

}
