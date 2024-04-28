package br.feevale.enums;

public enum TaskStatus {

	DO_NOT_STARTED(0),
	DOING(1),
	PAUSED(2),
	FINISHED(3),
	BLOCKED(4);

	private int ordinal;

	TaskStatus(int ordinal) {
		this.ordinal = ordinal;
	}

	public int getOrdinal() {
		return ordinal;
	}

}
