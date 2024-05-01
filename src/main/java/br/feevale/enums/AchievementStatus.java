package br.feevale.enums;

public enum AchievementStatus {

	DO_NOT_CONQUERED(0),
	CONQUERED(1);

	private int ordinal;

	AchievementStatus(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}

}
