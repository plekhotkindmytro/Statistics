package statistics.model.statistics;

import java.util.List;

public class PlayerStats {
	private String tshirtNumber;
	private List<EventStat> events;

	public PlayerStats() {
	}

	public PlayerStats(String tshirtNumber, List<EventStat> events) {
		this.tshirtNumber = tshirtNumber;
		this.events = events;
	}

	public String getTshirtNumber() {
		return tshirtNumber;
	}

	public void setTshirtNumber(String tshirtNumber) {
		this.tshirtNumber = tshirtNumber;
	}

	public List<EventStat> getEvents() {
		return events;
	}

	public void setEvents(List<EventStat> events) {
		this.events = events;
	}

}
