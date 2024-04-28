package br.feevale.controller;

import br.feevale.core.DefaultResponse;
import br.feevale.exceptions.CustomException;
import br.feevale.model.SessionModel;
import br.feevale.model.UserModel;
import br.feevale.service.impl.SessionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AuthController {

	@Autowired
	private SessionServiceImpl sessionService;

	@ResponseBody
	@PostMapping()
	public DefaultResponse<SessionModel> login(@RequestBody UserModel userParam) {
		try {
			return new DefaultResponse<>(sessionService.login(userParam));
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		}
	}

}
