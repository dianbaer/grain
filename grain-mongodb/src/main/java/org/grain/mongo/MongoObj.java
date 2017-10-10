package org.grain.mongo;

import org.bson.Document;

/**
 * 所有mongodb实体类需要继承的对象，需要保存Document才可以进行删除与修改操作
 *
 */
public class MongoObj {
	private Document document;

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

}
