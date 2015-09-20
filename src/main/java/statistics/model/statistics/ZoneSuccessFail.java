package statistics.model.statistics;

public class ZoneSuccessFail {
	private String gameName;
	private int scores;
	private int failedTries;

	public ZoneSuccessFail() {
	}

	public ZoneSuccessFail(String gameName, int scores, int failedTries) {
		this.gameName = gameName;
		this.scores = scores;
		this.failedTries = failedTries;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public int getScores() {
		return scores;
	}

	public void setScores(int scores) {
		this.scores = scores;
	}

	public int getFailedTries() {
		return failedTries;
	}

	public void setFailedTries(int failedTries) {
		this.failedTries = failedTries;
	}

}
