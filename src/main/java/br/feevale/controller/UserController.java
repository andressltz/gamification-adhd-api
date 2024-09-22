package br.feevale.controller;

import br.feevale.core.DefaultResponse;
import br.feevale.exceptions.CustomException;
import br.feevale.exceptions.UnauthorizedException;
import br.feevale.model.UserModel;
import br.feevale.service.LevelService;
import br.feevale.service.UserService;
import br.feevale.utils.UserUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

	private static final Logger LOG = LogManager.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private LevelService levelService;

	@ResponseBody
	@GetMapping()
	public DefaultResponse<UserModel> getUser(@RequestHeader HttpHeaders headers) {
		try {
			final UserModel loggedUser = getAuthUser(headers);
			loggedUser.setPassword(null);
			if (UserUtils.isPatient(loggedUser)) {
				levelService.setMaxLevelAndMaxStars(loggedUser);
				return new DefaultResponse<>(loggedUser);
			} else if (loggedUser.getPatients() != null && !loggedUser.getPatients().isEmpty()) {
				loggedUser.getPatients().forEach(pat -> {
					userService.cleanUser(pat);
					levelService.setMaxLevelAndMaxStars(pat);
				});
				return new DefaultResponse<>(loggedUser);
			}
			return new DefaultResponse<>(loggedUser);
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		} catch (UnauthorizedException ex) {
			return new DefaultResponse<>(ex);
		} catch (Exception ex) {
			return new DefaultResponse<>(ex);
		}
	}

	@ResponseBody
	@PostMapping()
	public DefaultResponse<UserModel> postUser(@RequestBody UserModel user) {
		try {
			return new DefaultResponse<>(userService.save(user, true, true));
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		} catch (UnauthorizedException ex) {
			return new DefaultResponse<>(ex);
		} catch (Exception ex) {
			return new DefaultResponse<>(ex);
		}
	}

	@ResponseBody
	@PatchMapping()
	public DefaultResponse<UserModel> updateUser(@RequestBody UserModel user) {
		try {
			return new DefaultResponse<>(userService.save(user, true, true));
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		} catch (UnauthorizedException ex) {
			return new DefaultResponse<>(ex);
		} catch (Exception ex) {
			return new DefaultResponse<>(ex);
		}
	}

	@ResponseBody
	@PostMapping("/patient")
	public DefaultResponse<UserModel> relatePatient(@RequestHeader HttpHeaders headers, @RequestBody UserModel patient) {
		try {
			final UserModel loggedUser = getAuthUser(headers);
			if (UserUtils.isNotPatient(loggedUser)) {
				return new DefaultResponse<>(userService.relatePatient(loggedUser, patient));
			}
			throw new CustomException("Operação não permitida para pacientes.");
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		}
	}

}
