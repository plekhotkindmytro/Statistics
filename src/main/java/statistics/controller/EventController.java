package statistics.controller;

import static spark.Spark.*;
import statistics.dao.EventDao;
import statistics.model.Event;

import com.google.gson.Gson;

public class EventController {
	private EventDao eventDao;

	public EventController(EventDao eventDao) {
		this.eventDao = eventDao;
		initializeRoutes();
	}

	private void initializeRoutes() {
		post("/events", (request, response) -> {
			Gson gson = new Gson();
			Event event = gson.fromJson(request.body(), Event.class);

			eventDao.add(event);
			return true;
		});

	}
}
