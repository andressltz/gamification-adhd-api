package br.feevale.controller;

import br.feevale.core.DefaultResponse;
import br.feevale.dtos.UserDto;
import br.feevale.exceptions.CustomException;
import br.feevale.model.UserModel;
import br.feevale.service.UserService;
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

	@Autowired
	private UserService userService;

	@ResponseBody
	@GetMapping()
	public DefaultResponse<UserDto> getUser(@RequestHeader HttpHeaders headers) {
		try {
			final UserDto loggedUser = getAuthUser(headers);
			return new DefaultResponse<>(loggedUser);
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		}
	}

	@ResponseBody
	@PostMapping()
	public DefaultResponse<UserDto> postUser(@RequestBody UserModel user) {
		try {
			return new DefaultResponse<>(userService.save(user));
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		}
	}

	@ResponseBody
	@PatchMapping()
	public DefaultResponse<UserDto> updateUser(@RequestBody UserModel user) {
		try {
			return new DefaultResponse<>(userService.save(user));
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		}
	}

	@ResponseBody
	@PostMapping("/patient")
	public DefaultResponse<UserDto> relatePatient(@RequestHeader HttpHeaders headers, @RequestBody UserDto patient) {
		try {
			final UserDto loggedUser = getAuthUser(headers);
			if (loggedUser.isNotPatient()) {
				return new DefaultResponse<>(userService.relatePatient(loggedUser, patient));
			}
			throw new CustomException("Operação não permitida para pacientes.");
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		}
	}

}
