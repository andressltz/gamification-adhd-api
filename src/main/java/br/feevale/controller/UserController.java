package br.feevale.controller;

import br.feevale.core.DefaultResponse;
import br.feevale.exceptions.CustomException;
import br.feevale.model.UserModel;
import br.feevale.service.SessionService;
import br.feevale.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private SessionService sessionService;

	@ResponseBody
	@GetMapping("/{token}")
	public DefaultResponse<UserModel> getUser(@PathVariable String token) {
		try {
			Long userId = sessionService.getAuthorizedUserId(token);
			return new DefaultResponse<>(userService.findById(userId));
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		}
	}

	@ResponseBody
	@PostMapping()
	public DefaultResponse<UserModel> postUser(@RequestBody UserModel user) {
		try {
			return new DefaultResponse<>(userService.save(user));
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		}
	}

	@ResponseBody
	@PatchMapping()
	public DefaultResponse<UserModel> updateUser(@RequestBody UserModel user) {
		try {
			return new DefaultResponse<>(userService.save(user));
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		}
	}

}
