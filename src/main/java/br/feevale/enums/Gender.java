package br.feevale.enums;

public enum Gender {

	NOT_SELECTED(0),
	FEMALE(1),
	MALE(2);

	private int ordinal;

	Gender(int ordinal) {
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
