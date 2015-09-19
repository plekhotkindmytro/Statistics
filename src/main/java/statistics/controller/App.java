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
import com.mongodb.client.MongoDatabase;

import freemarker.template.Configuration;

public class App {

	public static final List<Player> PLAYERS = new ArrayList<Player>();
	static {
		PLAYERS.add(new Player("Аліна Чупрунова", "17"));
		PLAYERS.add(new Player("Соня Дунаєва", "8"));
		PLAYERS.add(new Player("Соня Кастрикіна", "25"));
		PLAYERS.add(new Player("Аня", "5"));
		PLAYERS.add(new Player("Зазу", "14"));
		PLAYERS.add(new Player("Лєна", "15"));
		PLAYERS.add(new Player("Христя", "13"));
		PLAYERS.add(new Player("Танічка", "23"));
		PLAYERS.add(new Player("Свєта Бєдна", "3"));
		PLAYERS.add(new Player("Оксана Яковина", "33"));
		PLAYERS.add(new Player("Текіла", "7"));
	}
	private static final FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine(createFreemarkerConfiguration());

	private static Configuration createFreemarkerConfiguration() {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
		cfg.setClassForTemplateLoading(App.class, "/freemarker");
		cfg.setDefaultEncoding("UTF-8");
		return cfg;
	}

	public static void main(String[] args) {

		staticFileLocation("/public");
		exception(Exception.class, (e, request, response) -> {
			response.status(500);
			e.printStackTrace();
			response.body(e.getMessage());
		});

		get("/", (request, response) -> {
			Map<String, Object> attributes = new HashMap<>();

			attributes.put("players", PLAYERS);
			return new ModelAndView(attributes, "index.html");
		}, freeMarkerEngine);

		get("/players", (request, response) -> {
			return PLAYERS;
		}, new JsonTransformer());

		final MongoClient client = new MongoClient();
		final MongoDatabase database = client.getDatabase("frisbee");

		new EventController(new EventDao(database));
		new EventStatisticsController(new EventStatisticsDao(database));

	}
}