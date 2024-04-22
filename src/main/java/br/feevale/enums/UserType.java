package br.feevale.enums;

public enum UserType {

	PARENT(0),
	PATIENT(1),
	PROFESSIONAL(2);

	private int ordinal;

	UserType(int ordinal) {
		this.ordinal = ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public String getOrdinal() {
		return String.valueOf(ordinal);
	}

	public void setOrdinal(String ordinal) {
		this.ordinal = Integer.parseInt(ordinal);
	}

}
