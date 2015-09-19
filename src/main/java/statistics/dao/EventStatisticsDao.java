package statistics.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;
import org.bson.types.ObjectId;

import statistics.model.statistics.EventStat;
import statistics.model.statistics.GameScore;
import statistics.model.statistics.PlayerStats;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class EventStatisticsDao {

	private final MongoCollection<Document> collection;

	public EventStatisticsDao(MongoDatabase database) {
		this.collection = database.getCollection("event");
	}

	public void fixData() {
		List<Document> documents = new ArrayList<Document>();
		collection.find().sort(new Document("created", -1)).into(documents);
		for (int i = 0; i < documents.size(); i++) {
			Document document = documents.get(i);
			boolean player = document.containsKey("player");
			// boolean wrong = !player && (type.equals("score") ||
			// type.equals("drop") || type.equals("dropZone"));
			String type = document.getString("type");
			if (!player && !type.equals("start_game") && !type.equals("end_game")) {
				Document next = documents.get(i + 1);
				String nextType = next.getString("type");
				if (!nextType.equals(type)) {
					next.replace("type", nextType, type);
					ObjectId id = next.getObjectId("_id");
					next.remove("_id");
					collection.findOneAndReplace(new Document("_id", id), next);
					collection.deleteOne(new Document("_id", document.getObjectId("_id")));
					if (type.equals("score")) {
						Document thrower = documents.get(i + 2);
						String throwerType = thrower.getString("type");
						if (throwerType.startsWith("throw") && !throwerType.endsWith("-score")) {
							thrower.replace("type", throwerType, throwerType + "-score");
							ObjectId throwerId = thrower.getObjectId("_id");
							thrower.remove("_id");
							collection.findOneAndReplace(new Document("_id", throwerId), thrower);
						}
					}
				}
			}
		}
	}

	public List<Document> findAll() {
		return collection.find().into(new ArrayList<Document>());
	}

	public List<GameScore> getScoreCountByGame() {
		Document match = Document.parse("{$match: {type:\"score\"}}");
		Document group = Document.parse("{$group: { _id: \"$gameName\", scoreCount: {$sum:1}}}");

		List<Document> results = new ArrayList<Document>();
		collection.aggregate(Arrays.asList(match, group)).into(results);

		List<GameScore> gameScores = new ArrayList<GameScore>();
		for (Document document : results) {
			gameScores.add(new GameScore(document.getString("_id"), document.getInteger("scoreCount")));
		}
		return gameScores;
	}

	/**
	 * db.event.aggregate({$group: {_id: {player: "$player", type:
	 * "$type"},count: {$sum:1}}},{$sort: {"_id.player":1}}, {$project: {_id: 0,
	 * player: "$_id.player", type: "$_id.type", "count": 1}})
	 */
	public List<PlayerStats> getStatisticsByPlayer() {
		Document match = Document.parse("{$match: {type: {$nin: [\"start_game\", \"end_game\"]}}}");
		Document group = Document.parse("{$group: {_id: {player: \"$player\", type: \"$type\"},count: {$sum:1}}}");
		Document sort = Document.parse("{$sort: {\"_id.player\":1}}");
		Document project = Document.parse("{$project: {_id: 0, player: \"$_id.player\", type: \"$_id.type\", \"count\": 1}}");
		List<Document> results = new ArrayList<Document>();
		collection.aggregate(Arrays.asList(match, group, sort, project)).into(results);
		Map<String, List<EventStat>> map = new HashMap<String, List<EventStat>>();
		for (Document document : results) {
			final String number = document.getString("player");
			final String type = document.getString("type");
			final int count = document.getInteger("count");
			if (!map.containsKey(number)) {
				List<EventStat> stats = new ArrayList<EventStat>();
				map.put(number, stats);
			}
			map.get(number).add(new EventStat(type, count));
		}
		List<PlayerStats> playerStatsList = new ArrayList<PlayerStats>();
		for (Entry<String, List<EventStat>> entry : map.entrySet()) {
			playerStatsList.add(new PlayerStats(entry.getKey(), entry.getValue()));
		}
		return playerStatsList;
	}

	public PlayerStats getStatisticsByPlayer(String tshirtNumber) {
		Document match = Document.parse("{$match: {player:\"" + tshirtNumber + "\", type: {$nin: [\"start_game\", \"end_game\"]}}}");
		Document group = Document.parse("{$group: {_id: {type: \"$type\"},count: {$sum:1}}}");
		Document sort = Document.parse("{$sort: {\"_id.type\":1}}");
		Document project = Document.parse("{$project: {_id: 0, type: \"$_id.type\", \"count\": 1}}");
		List<Document> results = new ArrayList<Document>();
		collection.aggregate(Arrays.asList(match, group, sort, project)).into(results);
		List<EventStat> events = new ArrayList<EventStat>();
		for (Document document : results) {
			final String type = document.getString("type");
			final int count = document.getInteger("count");
			events.add(new EventStat(type, count));
		}

		return new PlayerStats(tshirtNumber, events);
	}
}
