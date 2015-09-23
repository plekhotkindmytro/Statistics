package statistics.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.bson.Document;
import org.bson.types.ObjectId;

import statistics.controller.App;
import statistics.model.Pass;
import statistics.model.Player;
import statistics.model.statistics.AttackCountFrequency;
import statistics.model.statistics.EventStat;
import statistics.model.statistics.GameScore;
import statistics.model.statistics.PassCountFrequency;
import statistics.model.statistics.PlayerStats;
import statistics.model.statistics.ZoneSuccessFail;

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
		Document sort = Document.parse("{$sort: {\"_id.player\":1, \"_id.type\":1}}");
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
			for (Player player : App.PLAYERS_FULL) {
				if (player.getNumber().equals(entry.getKey())) {
					playerStatsList.add(new PlayerStats(entry.getKey(), player.getName(), entry.getValue()));
					break;
				}
			}

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
		String name = null;
		for (Player player : App.PLAYERS_FULL) {
			if (player.getNumber().equals(tshirtNumber)) {
				name = player.getName();
				break;
			}
		}
		return new PlayerStats(tshirtNumber, name, events);
	}

	public List<ZoneSuccessFail> getZoneSuccessFail() {
		Document match = Document.parse("{$match: {type: {$in: [\"score\", \"dropZone\"]}}}");
		Document group = Document.parse("{$group: {_id: {\"game\":\"$gameName\", \"type\":\"$type\"}, count:{$sum:1}}}");
		Document project = Document.parse("{$project:{_id: 0, game: \"$_id.game\", type: \"$_id.type\", count: 1}}");
		Document sort = Document.parse("{$sort: {\"game\": 1}}");
		List<Document> results = new ArrayList<Document>();
		collection.aggregate(Arrays.asList(match, group, project, sort)).into(results);

		Map<String, List<EventStat>> map = new TreeMap<String, List<EventStat>>();
		for (Document document : results) {
			final String game = document.getString("game");
			if (!map.containsKey(game)) {
				map.put(game, new ArrayList<EventStat>());
			}
			final String type = document.getString("type");
			final Integer count = document.getInteger("count");
			map.get(game).add(new EventStat(type, count));
		}

		List<ZoneSuccessFail> successFailList = new ArrayList<ZoneSuccessFail>();
		for (Entry<String, List<EventStat>> entry : map.entrySet()) {
			List<EventStat> successFail = entry.getValue();
			int success = 0;
			int fail = 0;
			for (EventStat eventStat : successFail) {
				if (eventStat.getType().equals("score")) {
					success = eventStat.getCount();
				} else if (eventStat.getType().equals("dropZone")) {
					fail = eventStat.getCount();
				}
			}
			successFailList.add(new ZoneSuccessFail(entry.getKey(), success, fail));
		}

		return successFailList;
	}

	public List<PassCountFrequency> getPassCountFrequencySuccess() {
		List<Document> documents = new ArrayList<Document>();
		collection.find().sort(new Document("created", 1)).into(documents);
		int count = 0;
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
		for (Document document : documents) {
			String type = document.getString("type");
			if (type.startsWith("throw")) {
				count++;
			}

			if (type.equals("score")) {
				if (!map.containsKey(count)) {
					map.put(count, 1);
				} else {
					map.put(count, map.get(count) + 1);
				}
				count = 0;
			}

			if (type.equals("dropZone") || type.equals("drop") || type.equals("start_game") || type.equals("end_game")) {
				count = 0;
			}
		}

		List<PassCountFrequency> passCountFrequencies = new ArrayList<PassCountFrequency>();
		for (Entry<Integer, Integer> entry : map.entrySet()) {
			passCountFrequencies.add(new PassCountFrequency(entry.getKey(), entry.getValue()));
		}

		return passCountFrequencies;
	}

	public List<PassCountFrequency> getPassCountFrequencyFail() {
		List<Document> documents = new ArrayList<Document>();
		collection.find().sort(new Document("created", 1)).into(documents);
		int count = 0;
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
		for (Document document : documents) {
			String type = document.getString("type");
			if (type.startsWith("throw")) {
				count++;
			}

			if (type.equals("score") || type.equals("start_game") || type.equals("end_game")) {
				count = 0;
			}

			if (type.equals("dropZone") || type.equals("drop")) {
				if (!map.containsKey(count)) {
					map.put(count, 1);
				} else {
					map.put(count, map.get(count) + 1);
				}
				count = 0;
			}
		}

		List<PassCountFrequency> passCountFrequencies = new ArrayList<PassCountFrequency>();
		for (Entry<Integer, Integer> entry : map.entrySet()) {
			passCountFrequencies.add(new PassCountFrequency(entry.getKey(), entry.getValue()));
		}

		return passCountFrequencies;
	}

	public List<AttackCountFrequency> getAttackCountFrequency() {
		List<Document> documents = new ArrayList<Document>();
		collection.find().sort(new Document("created", 1)).into(documents);
		int count = 1;
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
		for (Document document : documents) {
			String type = document.getString("type");
			if (type.startsWith("drop")) {
				count++;
			}

			if (type.equals("score")) {
				if (!map.containsKey(count)) {
					map.put(count, 1);
				} else {
					map.put(count, map.get(count) + 1);
				}
				count = 1;
			}

			if (type.equals("start_game") || type.equals("end_game")) {
				count = 1;
			}
		}

		List<AttackCountFrequency> frequencies = new ArrayList<AttackCountFrequency>();
		for (Entry<Integer, Integer> entry : map.entrySet()) {
			frequencies.add(new AttackCountFrequency(entry.getKey(), entry.getValue()));
		}

		return frequencies;
	}

	public List<Pass> getPasses() {
		List<Document> documents = new ArrayList<Document>();
		collection.find().sort(new Document("created", 1)).into(documents);

		String from = null;

		List<Pass> passList = new ArrayList<Pass>();
		Pass currentPass = null;
		for (Document document : documents) {

			String type = document.getString("type");
			String player = document.getString("player");
			if (type.startsWith("throw")) {

				if (currentPass == null) {
					currentPass = new Pass();
				}
				if (from == null) {
					currentPass.setFrom(player);
					from = player;
				} else {
					currentPass.setTo(player);
					if (passList.contains(currentPass)) {
						passList.get(passList.indexOf(currentPass)).incCount();
					} else {
						passList.add(currentPass);
					}
					currentPass = new Pass();
					currentPass.setFrom(player);
					from = player;
				}
			} else if (type.equals("score")) {
				if (currentPass == null) {
					System.out.println(document);
				} else {
					currentPass.setTo(player);
					if (passList.contains(currentPass)) {
						passList.get(passList.indexOf(currentPass)).incCount();
					} else {
						passList.add(currentPass);
					}

					currentPass = null;
					from = null;
				}
			} else if (type.startsWith("drop")) {
				from = null;
				currentPass = null;
			} else if (type.equals("start_game") || type.equals("end_game")) {
				from = null;
				currentPass = null;
			}
		}

		Collections.sort(passList, new Comparator<Pass>() {
			@Override
			public int compare(Pass o1, Pass o2) {
				return o2.getCount() - o1.getCount();
			}
		});

		for (int i = 0; i < passList.size(); i++) {

			for (Player player : App.PLAYERS_FULL) {
				if (passList.get(i).getFrom().equals("Від: " + player.getNumber())) {
					passList.get(i).setFrom(player.getName());
				}

				if (passList.get(i).getTo().equals("До: " + player.getNumber())) {
					passList.get(i).setTo(player.getName());
				}
			}
		}
		int maxCountOfElements = 20;
		return passList.subList(0, maxCountOfElements > passList.size() ? passList.size() : maxCountOfElements);
	}
}
