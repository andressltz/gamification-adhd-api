package br.feevale.controller;

import br.feevale.core.DefaultResponse;
import br.feevale.exceptions.CustomException;
import br.feevale.exceptions.UnauthorizedException;
import br.feevale.model.SessionModel;
import br.feevale.model.UserModel;
import br.feevale.service.SessionService;
import br.feevale.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AuthController extends BaseController {

	@Autowired
	private SessionService sessionService;

	@ResponseBody
	@PostMapping()
	public DefaultResponse<SessionModel> login(@RequestHeader HttpHeaders headers, @RequestBody UserModel userParam) {
		try {
			return new DefaultResponse<>(sessionService.login(userParam, getAgent(headers)));
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		} catch (UnauthorizedException ex) {
			return new DefaultResponse<>(ex);
		} catch (Exception ex) {
			return new DefaultResponse<>(ex);
		}
	}

	@ResponseBody
	@PostMapping("/profile")
	public DefaultResponse<SessionModel> loginProfile(@RequestHeader HttpHeaders headers, @RequestBody UserModel userParam) {
		try {
			final UserModel loggedUser = getAuthUser(headers);
			if (UserUtils.isNotPatient(loggedUser)) {
				return new DefaultResponse<>(sessionService.loginProfile(userParam, loggedUser.getId(), getAgent(headers)));
			}
			throw new CustomException("Operação não permitida para pacientes.");
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		} catch (UnauthorizedException ex) {
			return new DefaultResponse<>(ex);
		} catch (Exception ex) {
			return new DefaultResponse<>(ex);
		}
	}

}
