package br.feevale.enums;

public enum UserType {

	PATIENT(1),
	PARENT(2),
	PROFESSIONAL(3);

	private int value;

	UserType(int value) {
		this.value = value;
	}

//	public int getValue() {
//		return value;
//	}

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
