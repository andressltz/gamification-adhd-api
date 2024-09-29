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
			if (UserUtils.isPatient(loggedUser)) {
				levelService.setMaxLevelAndMaxStars(loggedUser);
				if (loggedUser.getEmail() == null && loggedUser.getLoginUser() != null) {
					loggedUser.setEmail(loggedUser.getLoginUser().getEmail());
					loggedUser.setPhoneFormatted(loggedUser.getLoginUser().getPhoneFormatted());
				}
				loggedUser.setPassword(null);
				loggedUser.setLoginUser(null);
				return new DefaultResponse<>(loggedUser);
			} else if (loggedUser.getPatients() != null && !loggedUser.getPatients().isEmpty()) {
				loggedUser.getPatients().forEach(pat -> {
					if (pat.getLoginUser() != null && pat.getLoginUser().getId().equals(loggedUser.getId())) {
						pat.setProfile(true);
					}
					levelService.setMaxLevelAndMaxStars(pat);
					pat.setPassword(null);
					pat.setLoginUser(null);
				});
				loggedUser.setProfiles(userService.getProfiles(loggedUser));
//				if (loggedUser.getProfiles() != null && !loggedUser.getProfiles().isEmpty()) {
//					loggedUser.getProfiles().forEach(profile -> {
//						profile.setPatients(null);
//						userService.cleanUser(profile);
//					});
//				}
				loggedUser.setPassword(null);
				loggedUser.setLoginUser(null);
				return new DefaultResponse<>(loggedUser);
			}
			loggedUser.setPassword(null);
			loggedUser.setLoginUser(null);
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
			UserModel savedUser = userService.save(user, true, true);
			savedUser.setPassword(null);
			savedUser.setLoginUser(null);
			savedUser.setPatients(null);
			return new DefaultResponse<>(savedUser);
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
			UserModel savedUser = userService.save(user, true, true);
			savedUser.setPassword(null);
			savedUser.setLoginUser(null);
			savedUser.setPatients(null);
			return new DefaultResponse<>(savedUser);
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
				UserModel savedUser = userService.findAndRelatePatient(loggedUser, patient);
				savedUser.setPassword(null);
				savedUser.setLoginUser(null);
				savedUser.setPatients(null);
				return new DefaultResponse<>(savedUser);
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

	@ResponseBody
	@PostMapping("/patient/new")
	public DefaultResponse<UserModel> relateNewPatient(@RequestHeader HttpHeaders headers, @RequestBody UserModel patient) {
		try {
			final UserModel loggedUser = getAuthUser(headers);
			if (UserUtils.isNotPatient(loggedUser)) {
				UserModel savedUser = userService.registerAndRelatePatient(loggedUser, patient);
				savedUser.setPassword(null);
				savedUser.setLoginUser(null);
				savedUser.setPatients(null);
				return new DefaultResponse<>(savedUser);
			}
			throw new CustomException("Operação não permitida para pacientes.");
		} catch (CustomException ex) {
			getLogError(LOG, getAuthorization(headers), getAgent(headers), ex);
			return new DefaultResponse<>(ex);
		} catch (UnauthorizedException ex) {
			getLogError(LOG, getAuthorization(headers), getAgent(headers), ex);
			return new DefaultResponse<>(ex);
		} catch (Exception ex) {
			getLogError(LOG, getAuthorization(headers), getAgent(headers), ex);
			return new DefaultResponse<>(ex);
		}
	}

}
