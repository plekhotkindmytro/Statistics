package statistics.controller;

import static spark.Spark.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;

import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;
import statistics.dao.EventDao;
import statistics.dao.EventStatisticsDao;
import statistics.dao.FeedbackDao;
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
		PLAYERS_FULL.add(new Player("Ірина Алєксєєва", "7"));
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
		new FeedbackController(new FeedbackDao(database));
		new EventStatisticsController(new EventStatisticsDao(database));

	}

	private static class IpAddressFinder {

		private static final String[] HEADERS_TO_TRY = { "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED",
				"HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR" };

		public static String getClientIpAddress(HttpServletRequest request) {
			for (String header : HEADERS_TO_TRY) {
				final String ipString = request.getHeader(header);
				final String ip = getIpFromString(ipString);
				if (ip != null) {
					return ip;
				}
			}
			return request.getRemoteAddr();
		}

		private static String getIpFromString(String ipAddressesString) {
			if (StringUtils.isBlank(ipAddressesString)) {
				return null;
			}

			final String ips[] = StringUtils.split(ipAddressesString, ",");
			if (ips.length == 0) {
				return null;
			}

			if (!InetAddressValidator.getInstance().isValid(ips[0])) {
				return null;
			}

			return ips[0];
		}
	}
}
