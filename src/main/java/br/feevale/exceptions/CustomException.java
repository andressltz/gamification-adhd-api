package br.feevale.exceptions;

public class CustomException extends RuntimeException {

	public CustomException(String error) {
		super(error);
	}

	public CustomException(String error, Throwable cause) {
		super(error, cause);
	}
}
