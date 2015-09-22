package statistics.controller;

import static spark.Spark.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;
import statistics.dao.EventDao;
import statistics.dao.EventStatisticsDao;
import statistics.model.Player;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import freemarker.template.Configuration;

public class App {

	public static final List<Player> PLAYERS_FULL = new ArrayList<Player>();
	static {
		PLAYERS_FULL.add(new Player("Аліна Чупрунова", "17"));
		PLAYERS_FULL.add(new Player("Анна Войтюк", "5"));
		PLAYERS_FULL.add(new Player("Вікторія Газда", "14"));
		PLAYERS_FULL.add(new Player("Ірина Алексєєва", "7"));
		PLAYERS_FULL.add(new Player("Христина Скварок", "13"));
		PLAYERS_FULL.add(new Player("Оксана Яковина", "33"));
		PLAYERS_FULL.add(new Player("Олена Бойко", "15"));
		PLAYERS_FULL.add(new Player("Світлана Бєдна", "3"));
		PLAYERS_FULL.add(new Player("Софія Дунаєва", "8"));
		PLAYERS_FULL.add(new Player("Софія Кастрикіна", "25"));
		PLAYERS_FULL.add(new Player("Тетяна Мачулка", "23"));
	}

	public static final List<Player> PLAYERS = new ArrayList<Player>();
	static {
		PLAYERS.add(new Player("РђР»С–РЅР° Р§СѓРїСЂСѓРЅРѕРІР°", "17"));
		PLAYERS.add(new Player("РЎРѕРЅСЏ Р”СѓРЅР°С”РІР°", "8"));
		PLAYERS.add(new Player("РЎРѕРЅСЏ РљР°СЃС‚СЂРёРєС–РЅР°", "25"));
		PLAYERS.add(new Player("РђРЅСЏ", "5"));
		PLAYERS.add(new Player("Р—Р°Р·Сѓ", "14"));
		PLAYERS.add(new Player("Р›С”РЅР°", "15"));
		PLAYERS.add(new Player("РҐСЂРёСЃС‚СЏ", "13"));
		PLAYERS.add(new Player("РўР°РЅС–С‡РєР°", "23"));
		PLAYERS.add(new Player("РЎРІС”С‚Р° Р‘С”РґРЅР°", "3"));
		PLAYERS.add(new Player("РћРєСЃР°РЅР° РЇРєРѕРІРёРЅР°", "33"));
		PLAYERS.add(new Player("РўРµРєС–Р»Р°", "7"));
	}
	private static final FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine(createFreemarkerConfiguration());

	private static Configuration createFreemarkerConfiguration() {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
		cfg.setClassForTemplateLoading(App.class, "/freemarker");
		cfg.setDefaultEncoding("UTF-8");
		return cfg;
	}

	static int getHerokuAssignedPort() {
		ProcessBuilder processBuilder = new ProcessBuilder();
		if (processBuilder.environment().get("PORT") != null) {
			return Integer.parseInt(processBuilder.environment().get("PORT"));
		}
		return 4567; // return default port if heroku-port isn't set (i.e. on
						// localhost)
	}

	public static void main(String[] args) {
		port(getHerokuAssignedPort());

		staticFileLocation("/public");
		exception(Exception.class, (e, request, response) -> {
			response.status(500);
			e.printStackTrace();
			response.body(e.getMessage());
		});

		get("/", (request, response) -> {
			Map<String, Object> attributes = new HashMap<>();

			attributes.put("players", PLAYERS);
			return new ModelAndView(attributes, "statistics.html");
		}, freeMarkerEngine);

		// get("/admin", (request, response) -> {
		// Map<String, Object> attributes = new HashMap<>();
		//
		// attributes.put("players", PLAYERS);
		// return new ModelAndView(attributes, "index.html");
		// }, freeMarkerEngine);

		get("/players", (request, response) -> {
			return PLAYERS;
		}, new JsonTransformer());

		final String mongoClientUri;
		final String databaseName;
		final String mongoLabUri = System.getenv().get("MONGOLAB_URI");
		if (mongoLabUri == null) {
			mongoClientUri = "mongodb://localhost:27017/frisbee";
			databaseName = "frisbee";
		} else {
			mongoClientUri = mongoLabUri;
			databaseName = "heroku_q1k9ht7d";
		}
		final MongoClient client = new MongoClient(new MongoClientURI(mongoClientUri));
		final MongoDatabase database = client.getDatabase(databaseName);

		new EventController(new EventDao(database));
		new EventStatisticsController(new EventStatisticsDao(database));

	}
}
