package br.feevale.controller;

import br.feevale.model.UserModel;
import br.feevale.service.SessionService;
import br.feevale.service.UserService;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaseController {

	@Autowired
	private SessionService sessionService;

	@Autowired
	private UserService userService;

	protected UserModel getAuthUser(HttpHeaders headers) {
		String token = getAuthorization(headers);
		if (token == null) {
			return null;
		}
		Long userId = sessionService.getAuthorizedUserId(token.replace("Bearer ", ""));
		return userService.findByIdInternal(userId);
	}

	protected String getAuthorization(HttpHeaders headers) {
		return headers.getFirst("authorization");
	}

	protected String getAgent(HttpHeaders headers) {
		return headers.getFirst("user-agent");
	}

	protected void getLogError(Logger log, String auth, String agent, Exception ex) {
		log.error("[{}] - [{}] - [{}]", auth, agent, ex.getMessage(), ex);
	}

}
