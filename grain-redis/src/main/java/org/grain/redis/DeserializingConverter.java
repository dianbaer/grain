package org.grain.redis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.grain.log.ILog;

public class DeserializingConverter implements IConverter<byte[], Object> {
	private ILog log;

	public DeserializingConverter(ILog log) {
		this.log = log;
	}

	@Override
	public Object convert(byte[] source) {
		ByteArrayInputStream byteArrayInputStream = null;
		ObjectInputStream objectInputStream = null;
		try {
			byteArrayInputStream = new ByteArrayInputStream(source);
			objectInputStream = new ObjectInputStream(byteArrayInputStream);
			return objectInputStream.readObject();
		} catch (Exception e) {
			if (log != null) {
				log.error("redis反序列化异常", e);
			}
			return null;
		} finally {
			if (byteArrayInputStream != null) {
				try {
					byteArrayInputStream.close();
				} catch (IOException e) {
					if (log != null) {
						log.error("redi关闭流异常", e);
					}
				}
			}
			if (objectInputStream != null) {
				try {
					objectInputStream.close();
				} catch (IOException e) {
					if (log != null) {
						log.error("redi关闭流异常", e);
					}
				}
			}
		}
	}

}
