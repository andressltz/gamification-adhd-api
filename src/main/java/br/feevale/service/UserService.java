package br.feevale.service;

import br.feevale.enums.UserType;
import br.feevale.exceptions.CustomException;
import br.feevale.model.UserModel;
import br.feevale.repository.UserRepository;
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
public class UserService {

	private static final String PASS_SALT = "u2cHHUAIEDYKkDjCj2FkKHFKo1EtDuiBFEEVALE";
	private static final String REGEX_EMAIL = "[^\\s]+@[^\\s]+\\.[^\\s]+";
	private static final String REGEX_PASSWORD = "[^\\s]{6,10}";
	private static final String REGEX_PHONE = "[^0-9]";

	@Autowired
	private UserRepository repository;

	public UserModel save(UserModel user) {
		if (user.getId() == null) {
			return saveNewUser(user);
		} else {
			return update(user);
		}
	}

	public UserModel findByEmailAndPassword(String email, String password) {
		return repository.findByEmailAndPassword(email.toLowerCase().trim(), encryptPassword(password.trim()));
	}

	private UserModel saveNewUser(UserModel user) {
		validateUser(user);
		user.setEmail(user.getEmail().toLowerCase().trim());
		user.setPassword(encryptPassword(user.getPassword().trim()));

		user.setDtCreate(new Date());
		user.setDtUpdate(new Date());
		user = repository.save(user);
		cleanUser(user);
		return user;
	}

	private UserModel update(UserModel user) {
		validateUser(user);
		user.setDtUpdate(new Date());
		user = repository.save(user);
		cleanUser(user);
		return user;
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

	public UserModel findByIdInternal(Long userId) {
		UserModel userModel = repository.getReferenceById(userId);
		if (userModel.getPhone() != null) {
			userModel.setPhoneFormated(userModel.getPhone().replaceFirst("(\\d{2})(\\d{5})(\\d+)", "($1) $2-$3"));
		}
		return userModel;
	}

	public void cleanUser(UserModel user) {
		user.setPassword(null);
	}

	public UserModel relatePatient(UserModel loggedUser, UserModel patientParam) {
		if (patientParam != null) {
			UserModel patient = null;
			if (patientParam.getEmail() != null && !patientParam.getEmail().isEmpty()) {
				patient = repository.findByEmail(patientParam.getEmail().toLowerCase().trim());
			} else if (patientParam.getPhone() != null && !patientParam.getPhone().isEmpty()) {
				List<UserModel> results = repository.findByPhone(patientParam.getPhone().replaceAll(REGEX_PHONE, ""));
				if (results != null)
					if (results.size() > 1) {
						throw new CustomException("Encontrados alguns registros com o telefone informado. Por favor, busque pelo email.");
					} else {
						patient = results.stream().findFirst().get();
					}
			}

			if (patient == null) {
				throw new CustomException("Não foi possível localizar o paciente.");
			}

			if (!UserType.PATIENT.equals(patient.getType())) {
				throw new CustomException("Só é possível vincular pacientes.");
			}

			if (loggedUser.getPatients() == null || loggedUser.getPatients().isEmpty()) {
				loggedUser.setPatients(new ArrayList<>());
			} else if (loggedUser.getPatients().contains(patient)) {
				throw new CustomException("Paciente já esta vinculado.");
			}
			loggedUser.getPatients().add(patient);
			repository.save(loggedUser);

			return patient;

		}
		throw new CustomException("Não foi possível vincular o paciente.");
	}

	public void addStars(UserModel patient, int qtyStars) {
		if (patient != null && patient.getId() != null && qtyStars > 0) {
			int currentStars = patient.getQtyStars() != null ? patient.getQtyStars() : 0;
			currentStars = currentStars + qtyStars;
			patient.setQtyStars(currentStars);
			repository.save(patient);
		}
	}
}
