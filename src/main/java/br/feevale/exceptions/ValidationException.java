package br.feevale.exceptions;

public class ValidationException extends RuntimeException {

	public ValidationException(String error) {
		super(error);
	}

	public ValidationException(String error, Throwable cause) {
		super(error, cause);
	}
}
