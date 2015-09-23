package statistics.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import statistics.model.Event;
import statistics.model.EventType;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class FeedbackDao {

	private final MongoCollection<Document> collection;

	public FeedbackDao(MongoDatabase database) {
		this.collection = database.getCollection("email");
	}

	public List<Document> findAll() {
		return collection.find().into(new ArrayList<Document>());
	}

	public void add(String email) {
		Document document = new Document();
		document.append("created", new Date());
		document.append("email", email);

		collection.insertOne(document);
	}
}
