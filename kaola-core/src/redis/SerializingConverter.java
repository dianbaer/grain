package redis;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import log.LogManager;

public class SerializingConverter implements IConverter<Object, byte[]> {

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
			LogManager.redisLog.error("redis序列化异常", e);
			return null;
		} finally {
			if (byteArrayOutputStream != null) {
				try {
					byteArrayOutputStream.close();
				} catch (IOException e) {
					LogManager.redisLog.error("redi关闭流异常", e);
				}
			}
			if (objectOutputStream != null) {
				try {
					objectOutputStream.close();
				} catch (IOException e) {
					LogManager.redisLog.error("redi关闭流异常", e);
				}
			}
		}

	}

}
