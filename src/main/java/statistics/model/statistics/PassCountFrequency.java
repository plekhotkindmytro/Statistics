package statistics.model.statistics;

public class PassCountFrequency {

	private int passCount;
	private int frequency;

	public PassCountFrequency(int passCount, int frequency) {
		this.passCount = passCount;
		this.frequency = frequency;
	}

	public int getPassCount() {
		return passCount;
	}

	public void setPassCount(int passCount) {
		this.passCount = passCount;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
}
