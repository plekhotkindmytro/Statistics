package statistics.controller;

import static spark.Spark.*;
import statistics.dao.FeedbackDao;

public class FeedbackController {
	private FeedbackDao feedbackDao;

	public FeedbackController(FeedbackDao feedbackDao) {
		this.feedbackDao = feedbackDao;
		// TODO: set a security to it
		initializeRoutes();
	}

	private void initializeRoutes() {
		post("/emails", (request, response) -> {
			String email = request.body();
			if(email != null) {
				feedbackDao.add(email);
			}
			return true;
		});

	}
}
