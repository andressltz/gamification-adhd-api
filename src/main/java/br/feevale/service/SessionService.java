package br.feevale.service;

import br.feevale.exceptions.CustomException;
import br.feevale.model.SessionModel;
import br.feevale.model.UserModel;
import br.feevale.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
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

		SessionModel userSession = authorize(userRes);
		userService.cleanUser(userSession.getUser());
		userSession.setId(null);
		return userSession;
	}

	public long getAuthorizedUserId(String token) {
		SessionModel session = repository.getByToken(token);
		if (session != null && session.getExpiration().after(new Date())) {
			return session.getUser().getId();
		}
		throw new CustomException("Usuário não autorizado.");
	}

	private SessionModel authorize(UserModel user) {
		if (user == null) {
			return null;
		}

		List<SessionModel> sessions = repository.getByUser(user);
		if (sessions != null && !sessions.isEmpty()) {
			return sessions.stream().findFirst().get();
		}
		return createNewSession(user);
	}

	private SessionModel createNewSession(UserModel user) {
		SessionModel newSession = new SessionModel();
		newSession.setUser(user);
		newSession.setToken(UUID.randomUUID().toString());
		newSession.setExpiration(Date.from(LocalDateTime.now().plusDays(90).toInstant(ZoneOffset.of("-03:00"))));

		newSession.setDtCreate(new Date());
		newSession.setDtUpdate(new Date());
		repository.save(newSession);

		newSession.setId(null);
		userService.cleanUser(newSession.getUser());
		return newSession;
	}

}
