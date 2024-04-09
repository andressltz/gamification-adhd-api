package br.feevale.service;

import br.feevale.exceptions.CustomException;
import br.feevale.model.SessionModel;
import br.feevale.model.UserModel;
import br.feevale.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Component
public class SessionService {

	@Autowired
	private SessionRepository repository;

	@Autowired
	private UserService userService;

	public SessionModel login(UserModel userParam) {
		UserModel userRes = userService.findByEmailAndPassword(userParam.getEmail(), userParam.getPassword());
		if (userRes == null) {
			throw new CustomException("E-mail ou senha inválidos");
		}

		return authorize(userRes);
	}

	public SessionModel authorize(UserModel user) {
		SessionModel session = new SessionModel();
		session.setUser(user);
		session.setToken(UUID.randomUUID().toString());
		LocalDateTime dateExpiration = LocalDateTime.now().plusDays(90);
		session.setExpiration(Date.from(dateExpiration.atZone(ZoneId.systemDefault()).toInstant()));

		session.setDtCreate(new Date());
		session.setDtUpdate(new Date());
		repository.save(session);

		session.setId(null);
		session.setUser(null);
		return session;
	}

	public long getAuthorizedUserId(String token) {
		SessionModel session = repository.getByToken(token);
		if (session != null && session.getExpiration().after(new Date())) {
			return session.getUser().getId();
		}
		throw new CustomException("Usuário não autorizado.");
	}

}
