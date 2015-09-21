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

		get("/games/zoneSuccessFail", (request, response) -> {
			return eventStatisticsDao.getZoneSuccessFail();
		}, new JsonTransformer());
		
		get("/games/points/success/passCountFrequency", (request, response) -> {
			return eventStatisticsDao.getPassCountFrequencySuccess();
		}, new JsonTransformer());
		
		get("/games/points/fail/passCountFrequency", (request, response) -> {
			return eventStatisticsDao.getPassCountFrequencyFail();
		}, new JsonTransformer());
		
		get("/games/points/success/attacksCountFrequency", (request, response) -> {
			return eventStatisticsDao.getAttackCountFrequency();
		}, new JsonTransformer());

		// Reimplement as update
//		get("/fix", (request, response) -> {
//			eventStatisticsDao.fixData();
//			return true;
//		});
	}
}
