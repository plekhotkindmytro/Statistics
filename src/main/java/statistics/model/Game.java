package statistics.model;

import java.util.Date;

public class Game {

	private String Id;
	private Team teamA;
	private Team teamB;

	private Date created;

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public Team getTeamA() {
		return teamA;
	}

	public void setTeamA(Team teamA) {
		this.teamA = teamA;
	}

	public Team getTeamB() {
		return teamB;
	}

	public void setTeamB(Team teamB) {
		this.teamB = teamB;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

}
