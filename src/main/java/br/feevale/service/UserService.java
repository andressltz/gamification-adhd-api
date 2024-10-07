package br.feevale.service;

import br.feevale.enums.Gender;
import br.feevale.enums.UserType;
import br.feevale.exceptions.CustomException;
import br.feevale.exceptions.ValidationException;
import br.feevale.model.UserModel;
import br.feevale.repository.UserRepository;
import br.feevale.utils.CustomStringUtils;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

	private static final Logger LOG = LogManager.getLogger();

	private static final String PASS_SALT = "u2cHHUAIEDYKkDjCj2FkKHFKo1EtDuiBFEEVALE";
	private static final String REGEX_EMAIL = "[^\\s]+@[^\\s]+\\.[^\\s]+";
	private static final String REGEX_PASSWORD = "[^\\s]{6,10}";
	private static final String REGEX_PHONE = "[^0-9]";

	@Autowired
	private UserRepository repository;

	public UserModel save(UserModel user, boolean validatePass, boolean validateEmail) {
		return save(user, validatePass, validateEmail, false, null);
	}

	public UserModel saveNewPatientRelated(UserModel patient, UserModel userLoginToPatient) {
		return save(patient, false, false, true, userLoginToPatient);
	}

	public UserModel save(UserModel user, boolean validatePass, boolean validateEmail, boolean isNewPatientRelated, UserModel userLoginToPatient) {
		if (user.getGender() == null) {
			user.setGender(Gender.NOT_SELECTED);
		}
		if (user.getQtyStars() == null) {
			user.setQtyStars(0);
		}
		if (user.getLevel() == null) {
			user.setLevel(1);
		}

		user.setPhone(CustomStringUtils.numberOrNull(user.getPhone()));

		if (user.getId() == null) {
			return saveNewUser(user, isNewPatientRelated, userLoginToPatient);
		} else {
			return update(user, validatePass, validateEmail);
		}
	}

	public UserModel findByEmailAndPassword(String email, String password) {
		return repository.findByEmailAndPassword(email.toLowerCase().trim(), encryptPassword(password.trim()));
	}

	private UserModel saveNewUser(UserModel user, boolean isNewPatientRelated, UserModel userLoginToPatient) {
		try {
			if (isNewPatientRelated) {
				validateNewUserPatient(user);
				user.setEmail(null);
				user.setPassword(null);
				user.setLoginUser(userLoginToPatient);
			} else {
				validateUser(user, true, true);
				user.setEmail(user.getEmail().toLowerCase().trim());
				user.setPassword(encryptPassword(user.getPassword().trim()));
				user.setLoginUser(null);
			}

			user.setDtCreate(new Date());
			user.setDtUpdate(new Date());
			user = repository.save(user);
			return user;
		} catch (ValidationException ex) {
			LOG.error(ex.getMessage(), ex);
			throw new CustomException(ex.getMessage(), ex);
		} catch (Exception ex) {
			LOG.error("Erro ao salvar", ex);
			throw new CustomException("Erro ao salvar.", ex);
		}
	}

	private UserModel update(UserModel user, boolean validatePass, boolean validateEmail) {
		validateUser(user, validatePass, validateEmail);
		user.setDtUpdate(new Date());
		user = repository.save(user);
		return user;
	}

	private String encryptPassword(String password) {
		try {
			KeySpec spec = new PBEKeySpec(password.toCharArray(), PASS_SALT.getBytes(StandardCharsets.UTF_8), 256, 512);

			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

			return Base64.getEncoder().encodeToString(factory.generateSecret(spec).getEncoded());
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new CustomException("Erro ao salvar.", e);
		}
	}

	private void validateUser(@NotNull UserModel user, boolean validatePass, boolean validateEmail) throws CustomException {
		if (user.getEmail() == null || !user.getEmail().trim().matches(REGEX_EMAIL)) {
			throw new ValidationException("E-mail inválido.");
		}

		if (validatePass && (user.getPassword() == null || !user.getPassword().trim().matches(REGEX_PASSWORD))) {
			throw new ValidationException("Senha inválida. A senha deve conter entre 6 e 10 caracteres.");
		}

		if (user.getName() == null || user.getName().trim().equals("")) {
			throw new ValidationException("Nome inválido.");
		}

		if (user.getType() == null) {
			throw new ValidationException("Tipo de usuário não informado.");
		}

		if (validateEmail && repository.findByEmail(user.getEmail().toLowerCase().trim()) != null) {
			throw new ValidationException("E-mail já cadastrado.");
		}

		if (user.getPhone() != null && !user.getPhone().isEmpty() && user.getPhone().replaceAll(REGEX_PHONE, "").length() != 11) {
			throw new ValidationException("O telefone deve ser no formato (11) 98765-4321");
		}
	}

	private void validateNewUserPatient(@NotNull UserModel user) throws CustomException {
		if (user.getName() == null || user.getName().trim().equals("")) {
			throw new ValidationException("Nome inválido.");
		}

		if (user.getType() == null) {
			throw new ValidationException("Tipo de usuário não informado.");
		}

		if (user.getPhone() != null && !user.getPhone().isEmpty() && user.getPhone().replaceAll(REGEX_PHONE, "").length() != 11) {
			throw new ValidationException("O telefone deve ser no formato (11) 98765-4321");
		}
	}

	public UserModel findByIdInternal(Long userId) {
		try {
			UserModel userModel = repository.getReferenceById(userId);
			if (userModel.getPhone() != null) {
				userModel.setPhoneFormatted(CustomStringUtils.formatPhone(userModel.getPhone()));
			}
			userModel.setTotalDurationFormatted(CustomStringUtils.getDurationFormatted(userModel.getTotalDuration()));
			return userModel;
		} catch (EntityNotFoundException ex) {
			throw new CustomException("Usuário não localizado.");
		}
	}

	public UserModel findAndRelatePatient(UserModel loggedUser, UserModel patientParam) {
		if (patientParam != null) {
			UserModel patient = null;
			if (patientParam.getEmail() != null && !patientParam.getEmail().isEmpty()) {
				patient = repository.findByEmail(patientParam.getEmail().toLowerCase().trim());
			} else if (patientParam.getPhone() != null && !patientParam.getPhone().isEmpty()) {
				List<UserModel> results = repository.findByPhoneAndType(patientParam.getPhone().replaceAll(REGEX_PHONE, ""), UserType.PATIENT);
				if (results != null) {
					if (results.size() > 1) {
						throw new CustomException("Encontrados alguns registros com o telefone informado. Por favor, busque pelo email.");
					} else {
						patient = results.stream().findFirst().get();
					}
				}
			}

			if (patient == null) {
				throw new CustomException("Não foi possível localizar o paciente.");
			}
			return relatePatient(loggedUser, patient);

		}
		throw new CustomException("Não foi possível vincular o paciente.");
	}

	public UserModel relatePatient(UserModel loggedUser, UserModel existedPatient) {
		if (existedPatient != null) {
			if (!UserType.PATIENT.equals(existedPatient.getType())) {
				throw new CustomException("Só é possível vincular pacientes.");
			}

			if (loggedUser.getPatients() == null || loggedUser.getPatients().isEmpty()) {
				loggedUser.setPatients(new ArrayList<>());
			} else if (loggedUser.getPatients().contains(existedPatient)) {
				throw new CustomException("Paciente já esta vinculado.");
			}
			loggedUser.getPatients().add(existedPatient);
			repository.save(loggedUser);

			return existedPatient;
		}
		throw new CustomException("Não foi possível vincular o paciente.");
	}

	public int sumStars(Integer patientQtyStars, int qtyStars) {
		int currentStars = patientQtyStars != null ? patientQtyStars : 0;
		currentStars = currentStars + qtyStars;
		return currentStars;
	}

	public int lostStars(Integer patientQtyStars, int qtyStars) {
		int currentStars = patientQtyStars != null ? patientQtyStars : 0;
		currentStars = Math.max(currentStars - qtyStars, 0);
		return currentStars;
	}

	public UserModel registerAndRelatePatient(UserModel loggedUser, UserModel patient) {
		patient.setType(UserType.PATIENT);
		patient = saveNewPatientRelated(patient, loggedUser);
		return relatePatient(loggedUser, patient);
	}

	public List<UserModel> getProfiles(UserModel loggedUser) {
		List<UserModel> profiles = repository.findByLoginUser(loggedUser);

		List<UserModel> cleanProfiles = new ArrayList<>();
		if (profiles != null && !profiles.isEmpty()) {
			for (UserModel profile : profiles) {
				UserModel cleanUser = new UserModel();
				cleanUser.setId(profile.getId());
				cleanUser.setName(profile.getName());
				cleanProfiles.add(cleanUser);
			}
		}
		return cleanProfiles;
	}
}
