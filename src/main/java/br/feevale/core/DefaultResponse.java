package br.feevale.core;

import br.feevale.exceptions.CustomException;
import br.feevale.exceptions.UnauthorizedException;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DefaultResponse<T> {

	private T data;

	private HttpStatus statusInfo;

	private int status;

	private String error;

	public DefaultResponse(T data) {
		this.statusInfo = HttpStatus.ACCEPTED;
		this.status = statusInfo.value();
		this.data = data;
	}

	public DefaultResponse(Exception exception) {
		this.statusInfo = HttpStatus.BAD_REQUEST;
		this.status = statusInfo.value();
		this.error = exception.getMessage();
	}

	public DefaultResponse(CustomException exception) {
		this.statusInfo = HttpStatus.BAD_REQUEST;
		this.status = statusInfo.value();
		this.error = exception.getMessage();
	}

	public DefaultResponse(UnauthorizedException exception) {
		this.statusInfo = HttpStatus.UNAUTHORIZED;
		this.status = statusInfo.value();
		this.error = exception.getMessage();
	}

}
