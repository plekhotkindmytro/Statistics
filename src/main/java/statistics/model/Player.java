package statistics.model;

public class Player {
	private String id;
	private String name;
	private String number;
	private String disabled;

	public Player(String name, String number) {
		this.name = name;
		this.number = number;
		this.disabled = "";
	}

	public Player(String name, String number, String disabled) {
		this.name = name;
		this.number = number;
		this.disabled = disabled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisabled() {
		return disabled;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}
}
