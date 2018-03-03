package org.grain.mongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.grain.log.ILog;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class MongodbManager {
	public static MongoClient mongoClient;
	public static MongoDatabase mongoDatabase;
	public static String URL;
	public static int PORT;
	public static String USERNAME;
	public static String PASSWORD;
	public static String DBNAME;
	public static ILog log;

	/**
	 * 初始化mongodb
	 * 
	 * @param url
	 *            地址
	 * @param port
	 *            端口
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @param dbName
	 *            数据库名
	 * @param log
	 *            日志可以为null
	 */
	public static void init(String url, int port, String username, String password, String dbName, ILog log) {
		MongodbManager.URL = url;
		MongodbManager.PORT = port;
		MongodbManager.USERNAME = username;
		MongodbManager.PASSWORD = password;
		MongodbManager.DBNAME = dbName;
		MongodbManager.log = log;
		MongoCredential mongoCredential = MongoCredential.createCredential(username, dbName, password.toCharArray());
		mongoClient = new MongoClient(new ServerAddress(url, port), Arrays.asList(mongoCredential));
		mongoDatabase = mongoClient.getDatabase(MongodbManager.DBNAME);
	}

	/**
	 * 创建表
	 * 
	 * @param name
	 *            表名
	 * @return
	 */
	public static boolean createCollection(String name) {
		try {
			mongoDatabase.createCollection(name);
			return true;
		} catch (MongoCommandException e) {
			if (e.getCode() != 48) {
				if (log != null) {
					log.error("创建集合失败", e);
				}
			} else {
				if (log != null) {
					log.warn("创建集合失败,已存在此集合");
				}
			}
			return false;
		} catch (Exception e) {
			if (log != null) {
				log.error("创建集合失败", e);
			}
			return false;
		}
	}

	/**
	 * 获取表的链接
	 * 
	 * @param collectionName
	 *            表名
	 * @return
	 */
	public static MongoCollection<Document> getCollection(String collectionName) {
		MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
		return collection;
	}

	/**
	 * 对象转mongodb格式
	 * 
	 * @param obj
	 * @return
	 */
	public static Document objectToDocument(Object obj) {
		Gson gson = new Gson();
		String objStr = gson.toJson(obj);
		Document document = Document.parse(objStr);
		return document;
	}

	/**
	 * mongodb转对象格式
	 * 
	 * @param document
	 * @param clazz
	 * @return
	 */
	public static <T> T documentToObject(Document document, Class<T> clazz) {
		Gson gson = new Gson();
		String objStr = document.toJson();
		T obj = gson.fromJson(objStr, clazz);
		return obj;
	}

	/**
	 * 插入一条记录
	 * 
	 * @param collectionName
	 *            表名
	 * @param mongoObj
	 *            记录
	 * @return
	 */
	public static boolean insertOne(String collectionName, MongoObj mongoObj) {
		MongoCollection<Document> collection = getCollection(collectionName);
		try {
			Document document = objectToDocument(mongoObj);
			collection.insertOne(document);
			return true;
		} catch (Exception e) {
			if (log != null) {
				log.error("插入document失败", e);
			}
			return false;
		}

	}

	/**
	 * 插入list
	 * 
	 * @param collectionName
	 *            表名
	 * @param list
	 *            list
	 * @return
	 */
	public static boolean insertMany(String collectionName, List<MongoObj> list) {
		MongoCollection<Document> collection = getCollection(collectionName);
		try {
			ArrayList<Document> documentList = new ArrayList<>();
			for (int i = 0; i < list.size(); i++) {
				MongoObj mongoObj = list.get(i);
				Document document = objectToDocument(mongoObj);
				documentList.add(document);
			}
			collection.insertMany(documentList);
			return true;
		} catch (Exception e) {
			if (log != null) {
				log.error("插入documentList失败", e);
			}
			return false;
		}
	}

	/**
	 * 查询列表
	 * 
	 * @param collectionName
	 *            表名
	 * @param filter
	 *            过滤条件
	 * @param clazz
	 *            类名
	 * @param start
	 *            开始条数
	 * @param pageSize
	 *            多少条
	 * @return
	 */
	public static <T> List<T> find(String collectionName, Bson filter, Class<T> clazz, int start, int pageSize) {
		MongoCollection<Document> collection = getCollection(collectionName);
		try {
			MongoCursor<Document> iterator = null;
			if (pageSize == 0) {
				if (filter == null) {
					iterator = collection.find().iterator();
				} else {
					iterator = collection.find(filter).iterator();
				}
			} else {
				if (filter == null) {
					iterator = collection.find().skip(start).limit(pageSize).iterator();
				} else {
					iterator = collection.find(filter).skip(start).limit(pageSize).iterator();
				}
			}
			ArrayList<T> list = new ArrayList<>();
			while (iterator.hasNext()) {
				Document document = iterator.next();
				T obj = documentToObject(document, clazz);
				MongoObj mongoObj = (MongoObj) obj;
				mongoObj.setDocument(document);
				list.add(obj);
			}
			return list;
		} catch (Exception e) {
			if (log != null) {
				log.error("查询documentList失败", e);
			}
			return null;
		}
	}

	/**
	 * 删除记录
	 * 
	 * @param collectionName
	 *            表名
	 * @param mongoObj
	 *            记录
	 * @return
	 */
	public static boolean deleteById(String collectionName, MongoObj mongoObj) {
		MongoCollection<Document> collection = getCollection(collectionName);
		try {
			Bson filter = Filters.eq(MongoConfig.MONGO_ID, mongoObj.getDocument().getObjectId(MongoConfig.MONGO_ID));
			DeleteResult result = collection.deleteOne(filter);
			if (result.getDeletedCount() == 1) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			if (log != null) {
				log.error("删除记录失败", e);
			}
			return false;
		}

	}

	/**
	 * 修改记录
	 * 
	 * @param collectionName
	 *            表名
	 * @param mongoObj
	 *            对象
	 * @return
	 */
	public static boolean updateById(String collectionName, MongoObj mongoObj) {
		MongoCollection<Document> collection = getCollection(collectionName);
		try {
			Bson filter = Filters.eq(MongoConfig.MONGO_ID, mongoObj.getDocument().getObjectId(MongoConfig.MONGO_ID));
			mongoObj.setDocument(null);
			Document document = objectToDocument(mongoObj);
			UpdateResult result = collection.updateOne(filter, new Document(MongoConfig.$SET, document));
			if (result.getMatchedCount() == 1) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			if (log != null) {
				log.error("修改记录失败", e);
			}
			return false;
		}

	}

	/**
	 * 获取个数
	 * 
	 * @param collectionName
	 *            表名
	 * @param filter
	 *            过滤
	 * @return
	 */
	public static long count(String collectionName, Bson filter) {
		MongoCollection<Document> collection = getCollection(collectionName);
		try {
			if (filter == null) {
				return collection.count();
			} else {
				return collection.count(filter);
			}
		} catch (Exception e) {
			if (log != null) {
				log.error("查询个数失败", e);
			}
			return 0;
		}

	}
}
