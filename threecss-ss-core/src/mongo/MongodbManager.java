package mongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;

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

import log.LogManager;

public class MongodbManager {
	public static MongoClient mongoClient;
	public static MongoDatabase mongoDatabase;
	public static String URL;
	public static int PORT;
	public static String USERNAME;
	public static String PASSWORD;
	public static String DBNAME;

	public static void init(String url, int port, String username, String password, String dbName) {
		MongodbManager.URL = url;
		MongodbManager.PORT = port;
		MongodbManager.USERNAME = username;
		MongodbManager.PASSWORD = password;
		MongodbManager.DBNAME = dbName;
		MongoCredential mongoCredential = MongoCredential.createCredential(username, dbName, password.toCharArray());
		mongoClient = new MongoClient(new ServerAddress(url, port), Arrays.asList(mongoCredential));
		mongoDatabase = mongoClient.getDatabase(MongodbManager.DBNAME);
	}

	public static boolean createCollection(String name) {
		try {
			mongoDatabase.createCollection(name);
			return true;
		} catch (MongoCommandException e) {
			if (e.getCode() != 48) {
				LogManager.mongodbLog.error("创建集合失败", e);
			} else {
				LogManager.mongodbLog.warn("创建集合失败,已存在此集合");
			}
			return false;
		} catch (Exception e) {
			LogManager.mongodbLog.error("创建集合失败", e);
			return false;
		}
	}

	public static MongoCollection<Document> getCollection(String collectionName) {
		MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
		return collection;
	}

	public static Document objectToDocument(Object obj) {
		Gson gson = new Gson();
		String objStr = gson.toJson(obj);
		Document document = Document.parse(objStr);
		return document;
	}

	public static <T> T documentToObject(Document document, Class<T> clazz) {
		Gson gson = new Gson();
		String objStr = document.toJson();
		T obj = gson.fromJson(objStr, clazz);
		return obj;
	}

	public static boolean insertOne(String collectionName, MongoObj mongoObj) {
		MongoCollection<Document> collection = getCollection(collectionName);
		try {
			Document document = objectToDocument(mongoObj);
			collection.insertOne(document);
			return true;
		} catch (Exception e) {
			LogManager.mongodbLog.error("插入document失败", e);
			return false;
		}

	}

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
			LogManager.mongodbLog.error("插入documentList失败", e);
			return false;
		}
	}

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
			LogManager.mongodbLog.error("查询documentList失败", e);
			return null;
		}
	}

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
			LogManager.mongodbLog.error("删除记录失败", e);
			return false;
		}

	}

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
			LogManager.mongodbLog.error("修改记录失败", e);
			return false;
		}

	}

	public static long count(String collectionName, Bson filter) {
		MongoCollection<Document> collection = getCollection(collectionName);
		try {
			if (filter == null) {
				return collection.count();
			} else {
				return collection.count(filter);
			}
		} catch (Exception e) {
			LogManager.mongodbLog.error("查询个数失败", e);
			return 0;
		}

	}
}
