package br.feevale.service;

import br.feevale.dtos.UserDto;
import br.feevale.model.UserModel;

public interface UserService {

	UserDto save(UserModel user);

	UserDto relatePatient(UserDto loggedUser, UserDto patientParam);

	UserDto findByIdInternal(Long userId);

	UserModel findByEmailAndPassword(String email, String password);
}
