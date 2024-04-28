package br.feevale.controller;

import br.feevale.dtos.UserDto;
import br.feevale.service.UserService;
import br.feevale.service.impl.SessionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {

	@Autowired
	private SessionServiceImpl sessionService;

	@Autowired
	private UserService userService;

	protected UserDto getAuthUser(HttpHeaders headers) {
		String token = headers.getFirst("authorization");
		if (token == null) {
			return null;
		}
		Long userId = sessionService.getAuthorizedUserId(token.replace("Bearer ", ""));
		return userService.findByIdInternal(userId);
	}

}
