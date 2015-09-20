package statistics.model.statistics;

import java.util.List;

public class PlayerStats {
	private String tshirtNumber;
	private String name;
	private List<EventStat> events;

	public PlayerStats() {
	}

	public PlayerStats(String tshirtNumber, String name, List<EventStat> events) {
		this.tshirtNumber = tshirtNumber;
		this.name = name;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
