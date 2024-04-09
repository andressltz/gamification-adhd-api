package br.feevale.enums;

public enum TaskStatus {

	DO_NOT_STARTED(1),
	DOING(2),
	PAUSED(3),
	FINISHED(4);

	private int value;

	TaskStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
