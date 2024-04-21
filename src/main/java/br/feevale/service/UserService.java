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

//	public User findById(Long userId) {
//		User user = repository.findById(userId).orElse(null);
//		if (user != null) {
//			user.setPassword(null);
//			return user;
//		}
//		return user;
//	}

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
//		UserModel currentUser = repository.findById(user.getId()).orElse(null);

//		if (currentUser == null) {
//			throw new CustomException("Usuário inválido.");
//		}

//		user.setEmail(currentUser.getEmail());

//		if (user.getPassword() != null && !user.getPassword().trim().equals("")
//			&& user.getCurrentpassword() != null && !user.getCurrentpassword().trim().equals("")) {
//
//			if (!encryptPassword(user.getCurrentpassword()).equals(currentUser.getPassword())) {
//				throw new CustomException("Senha incorreta.");
//			}
//
//			if (!user.getPassword().matches(passwordRegex)) {
//				throw new CustomException("Senha inválida.");
//			}
//
//			if (!user.getConfpassword().equals(user.getPassword())) {
//				throw new CustomException("Senha não conferem.");
//			}
//
//			user.setPassword(encryptPassword(user.getPassword()));
//		} else {
//			user.setPassword(currentUser.getPassword());
//		}
//
//		if (user.getName() == null || user.getName().trim().equals("")) {
//			throw new CustomException("Nome inválido");
//		}

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

	public UserModel findById(Long userId) {
		UserModel user = repository.getReferenceById(userId);
		cleanUser(user);
		return user;
	}

	public UserModel findByIdInternal(Long userId) {
		return repository.getReferenceById(userId);
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
}
