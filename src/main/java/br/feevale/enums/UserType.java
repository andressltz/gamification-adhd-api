package br.feevale.enums;

public enum UserType {

	PARENT(1, 0),
	PATIENT(2, 1),
	PROFESSIONAL(3, 2);

	private int value;

	private int ordinal;

	UserType(int value, int ordinal) {
		this.value = value;
		this.ordinal = ordinal;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getValue() {
		return String.valueOf(value);
	}

	public void setValue(String value) {
		this.value = Integer.parseInt(value);
	}

}
