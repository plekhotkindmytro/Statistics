package statistics.model.statistics;

public class AttackCountFrequency {

	private int attackCount;
	private int frequency;

	public AttackCountFrequency(int attackCount, int frequency) {
		this.setAttackCount(attackCount);
		this.frequency = frequency;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getAttackCount() {
		return attackCount;
	}

	public void setAttackCount(int attackCount) {
		this.attackCount = attackCount;
	}
}
