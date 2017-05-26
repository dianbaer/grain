package redis;

import java.io.Serializable;

import log.LogManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisManager {
	private static JedisPool jedisPool;
	private static IConverter<Object, byte[]> serializer;
	private static IConverter<byte[], Object> deserializer;

	public static void init(String ip, int port) {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(2000);
		jedisPoolConfig.setMaxIdle(200);
		jedisPoolConfig.setMinIdle(0);
		jedisPoolConfig.setTestOnBorrow(true);
		jedisPool = new JedisPool(jedisPoolConfig, ip, port);
		serializer = new SerializingConverter();
		deserializer = new DeserializingConverter();
	}

	public static Jedis getJedis() {
		return jedisPool.getResource();
	}

	public static String getStringValue(String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();

			String result = jedis.get(key);
			return result;
		} catch (Exception e) {
			LogManager.redisLog.error("redis获取string失败", e);
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public static boolean setStringValue(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			String result = jedis.set(key, value);
			return result.equals("OK") ? true : false;
		} catch (Exception e) {
			LogManager.redisLog.error("redis设置string失败", e);
			return false;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public static Object getObjValue(Serializable key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			byte[] keyByte = jedis.get(serializer.convert(key));
			if (keyByte == null) {
				return null;
			}
			Object result = deserializer.convert(keyByte);
			return result;
		} catch (Exception e) {
			LogManager.redisLog.error("redis获取obj失败", e);
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public static boolean setObjValue(Serializable key, Serializable value) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			String result = jedis.set(serializer.convert(key), serializer.convert(value));
			return result.equals("OK") ? true : false;
		} catch (Exception e) {
			LogManager.redisLog.error("redis设置obj失败", e);
			return false;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
}
