package br.feevale.service.impl;

import br.feevale.dtos.UserDto;
import br.feevale.enums.UserType;
import br.feevale.exceptions.CustomException;
import br.feevale.model.UserModel;
import br.feevale.repository.UserRepository;
import br.feevale.service.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class UserServiceImpl implements UserService {

	private static final String PASS_SALT = "u2cHHUAIEDYKkDjCj2FkKHFKo1EtDuiBFEEVALE";
	private static final String REGEX_EMAIL = "[^\\s]+@[^\\s]+\\.[^\\s]+";
	private static final String REGEX_PASSWORD = "[^\\s]{6,10}";
	private static final String REGEX_PHONE = "[^0-9]";

	@Autowired
	private UserRepository repository;

	public UserDto save(UserModel user) {
		if (user.getId() == null) {
			return saveNewUser(user);
		} else {
			return update(user);
		}
	}

	public UserModel findByEmailAndPassword(String email, String password) {
		return repository.findByEmailAndPassword(email.toLowerCase().trim(), encryptPassword(password.trim()));
	}

	private UserDto saveNewUser(UserModel user) {
		validateUser(user);
		user.setEmail(user.getEmail().toLowerCase().trim());
		user.setPassword(encryptPassword(user.getPassword().trim()));

		user.setDtCreate(new Date());
		user.setDtUpdate(new Date());
		user = repository.save(user);
		return new UserDto(user);
	}

	private UserDto update(UserModel user) {
		validateUser(user);
		user.setDtUpdate(new Date());
		user = repository.save(user);
		return new UserDto(user);
	}

	private String encryptPassword(String password) {
		try {
			KeySpec spec = new PBEKeySpec(password.toCharArray(), PASS_SALT.getBytes(StandardCharsets.UTF_8), 256, 512);

			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

			return Base64.getEncoder().encodeToString(factory.generateSecret(spec).getEncoded());
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new CustomException("Erro ao salvar.");
		}
	}

	private void validateUser(@NotNull UserModel user) throws CustomException {
		if (user.getEmail() == null || !user.getEmail().trim().matches(REGEX_EMAIL)) {
			throw new CustomException("E-mail inválido.");
		}

		if (user.getPassword() == null || !user.getPassword().trim().matches(REGEX_PASSWORD)) {
			throw new CustomException("Senha inválida. A senha deve conter entre 6 e 10 caracteres.");
		}

		if (user.getName() == null || user.getName().trim().equals("")) {
			throw new CustomException("Nome inválido.");
		}

		if (user.getType() == null) {
			throw new CustomException("Tipo de usuário não informado.");
		}

		if (repository.findByEmail(user.getEmail().toLowerCase().trim()) != null) {
			throw new CustomException("E-mail já cadastrado.");
		}

		if (user.getPhone() != null && !user.getPhone().isEmpty() && user.getPhone().replaceAll(REGEX_PHONE, "").length() != 11) {
			throw new CustomException("O telefone deve ser no formato (11) 98765-4321");
		}
	}

//	public UserModel findById(Long userId) {
//		UserModel user = repository.getReferenceById(userId);
//		cleanUser(user);
//		return user;
//	}

	public UserDto findByIdInternal(Long userId) {
		UserModel userModel = repository.getReferenceById(userId);
		UserDto dto = new UserDto(userModel);
		if (dto.getPhone() != null) {
			dto.setPhoneFormated(userModel.getPhone().replaceFirst("(\\d{2})(\\d{5})(\\d+)", "($1) $2-$3"));
		}
		return dto;
	}

	public UserDto relatePatient(UserDto loggedUser, UserDto patientParam) {
		if (patientParam != null) {
			UserModel patientModel = null;
			if (patientParam.getEmail() != null && !patientParam.getEmail().isEmpty()) {
				patientModel = repository.findByEmail(patientParam.getEmail().toLowerCase().trim());
			} else if (patientParam.getPhone() != null && !patientParam.getPhone().isEmpty()) {
				List<UserModel> results = repository.findByPhone(patientParam.getPhone().replaceAll(REGEX_PHONE, ""));
				if (results != null)
					if (results.size() > 1) {
						throw new CustomException("Encontrados alguns registros com o telefone informado. Por favor, busque pelo email.");
					} else {
						patientModel = results.stream().findFirst().get();
					}
			}

			if (patientModel == null) {
				throw new CustomException("Não foi possível localizar o paciente.");
			}

			if (!UserType.PATIENT.equals(patientModel.getType())) {
				throw new CustomException("Só é possível vincular pacientes.");
			}

			if (loggedUser.getPatients() == null || loggedUser.getPatients().isEmpty()) {
				loggedUser.setPatients(new ArrayList<>());
			} else if (loggedUser.getPatients().contains(patientModel)) {
				throw new CustomException("Paciente já esta vinculado.");
			}
			loggedUser.getPatients().add(patientModel);
			repository.save(loggedUser);

			return new UserDto(patientModel);

		}
		throw new CustomException("Não foi possível vincular o paciente.");
	}
}
