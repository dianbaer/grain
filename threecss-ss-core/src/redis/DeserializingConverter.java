package redis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import log.LogManager;

public class DeserializingConverter implements IConverter<byte[], Object> {

	@Override
	public Object convert(byte[] source) {
		ByteArrayInputStream byteArrayInputStream = null;
		ObjectInputStream objectInputStream = null;
		try {
			byteArrayInputStream = new ByteArrayInputStream(source);
			objectInputStream = new ObjectInputStream(byteArrayInputStream);
			return objectInputStream.readObject();
		} catch (Exception e) {
			LogManager.redisLog.error("redis反序列化异常", e);
			return null;
		} finally {
			if (byteArrayInputStream != null) {
				try {
					byteArrayInputStream.close();
				} catch (IOException e) {
					LogManager.redisLog.error("redi关闭流异常", e);
				}
			}
			if (objectInputStream != null) {
				try {
					objectInputStream.close();
				} catch (IOException e) {
					LogManager.redisLog.error("redi关闭流异常", e);
				}
			}
		}
	}

}
