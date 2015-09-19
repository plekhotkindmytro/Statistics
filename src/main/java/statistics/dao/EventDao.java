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

public class EventDao {

	private final MongoCollection<Document> collection;

	public EventDao(MongoDatabase database) {
		this.collection = database.getCollection("event");
	}

	public List<Document> findAll() {
		return collection.find().into(new ArrayList<Document>());
	}

	public void add(Event event) {
		Document document = new Document();
		document.append("created", new Date());
		document.append("gameName", event.getGameName());
		document.append("type", event.getType());
		if (event.getPlayer() != null) {
			document.append("player", event.getPlayer());
		}
		collection.insertOne(document);
	}

	public void update(String eventId, String gameId, String teamId, String playerId, EventType type) {
		Document document = new Document("gameId", gameId);
		document.append("teamId", teamId);
		document.append("playerId", playerId);
		document.append("type", type);
		document.append("created", new Date());
		collection.updateOne(new Document("_id", new ObjectId(eventId)), document);
	}

	public void delete(String eventId) {
		collection.deleteOne(new Document("_id", new ObjectId(eventId)));
	}

}
