package br.feevale.service;

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
import java.util.Base64;
import java.util.Date;

@Component
public class UserService {

	private static final String PASS_SALT = "u2cHHUAIEDYKkDjCj2FkKHFKo1EtDuiBFEEVALE";
	private static final String REGEX_EMAIL = "[^\\s]+@[^\\s]+\\.[^\\s]+";
	private static final String REGEX_PASSWORD = "[^\\s]{6,10}";

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
		return repository.findByEmailAndPassword(email, encryptPassword(password));
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
		user.setPassword(encryptPassword(user.getPassword()));

		user.setDtCreate(new Date());
		user.setDtUpdate(new Date());
		user = repository.save(user);
		user.setPassword(null);
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
		user.setPassword(null);
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

	private void validateUser(@NotNull UserModel user) throws RuntimeException {
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

		if (repository.findByEmail(user.getEmail().trim()) != null) {
			throw new CustomException("E-mail já cadastrado.");
		}
	}

	public UserModel findById(Long userId) {
		UserModel user = repository.getReferenceById(userId);
		user.setPassword(null);
		return user;
	}

}
