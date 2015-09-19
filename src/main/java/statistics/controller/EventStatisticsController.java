package statistics.controller;

import static spark.Spark.*;
import statistics.dao.EventStatisticsDao;

public class EventStatisticsController {
	private EventStatisticsDao eventStatisticsDao;

	public EventStatisticsController(EventStatisticsDao eventStatisticsDao) {
		this.eventStatisticsDao = eventStatisticsDao;
		initializeRoutes();
	}

	private void initializeRoutes() {
		get("/games/scores", (request, response) -> {
			return eventStatisticsDao.getScoreCountByGame();
		}, new JsonTransformer());

		get("/players/stats", (request, response) -> {
			return eventStatisticsDao.getStatisticsByPlayer();
		}, new JsonTransformer());
		
		get("/players/:number/stats", (request, response) -> {
			String tshirtNumber = request.params(":number");
			return eventStatisticsDao.getStatisticsByPlayer(tshirtNumber);
		}, new JsonTransformer());

		get("/fix", (request, response) -> {
			eventStatisticsDao.fixData();
			return true;
		});
	}
}
