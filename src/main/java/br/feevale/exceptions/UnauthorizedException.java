package br.feevale.exceptions;

public class UnauthorizedException extends RuntimeException {

	public UnauthorizedException(String error) {
		super(error);
	}

	public UnauthorizedException(String error, Throwable cause) {
		super(error, cause);
	}
}
