package br.feevale.service;

import br.feevale.enums.UserType;
import br.feevale.exceptions.CustomException;
import br.feevale.exceptions.UnauthorizedException;
import br.feevale.model.SessionModel;
import br.feevale.model.UserModel;
import br.feevale.repository.SessionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class SessionService {

	private static final Logger LOGGER = LogManager.getLogger();

	@Autowired
	private SessionRepository repository;

	@Autowired
	private UserService userService;

	public SessionModel login(UserModel userParam) {
		if (userParam != null && userParam.getEmail() != null && userParam.getPassword() != null) {
			UserModel userRes = userService.findByEmailAndPassword(userParam.getEmail(), userParam.getPassword());
			if (userRes != null) {
				SessionModel userSession = authorize(userRes);
				userService.cleanUser(userSession.getUser());
				userSession.setId(null);
				userRes = userSession.getUser();
				userRes.setPatients(null);
				userSession.setUser(userRes);
				return userSession;
			}
		}

		throw new CustomException("E-mail ou senha inválidos");
	}

	public SessionModel loginProfile(UserModel userParam, UserModel loggedUser) {
		if (userParam != null && userParam.getId() != null) {
			UserModel userRes = userService.findByIdInternal(userParam.getId());
			if (userRes != null && UserType.PATIENT.equals(userRes.getType()) && userRes.getLoginUser() != null && loggedUser.getId().equals(userRes.getLoginUser().getId())) {
				SessionModel userSession = authorize(userRes);
				userService.cleanUser(userSession.getUser());
				userSession.setId(null);
				userRes = userSession.getUser();
				userRes.setPatients(null);
				userSession.setUser(userRes);
				return userSession;
			}
		}

		throw new CustomException("Login não permitido.");
	}

	public long getAuthorizedUserId(String token) {
		SessionModel session = repository.getByToken(token);
		if (session != null) {
			return session.getUser().getId();
		}
		LOGGER.warn("Usuário {} não autorizado. Refaça o login.", token);
		throw new UnauthorizedException("Usuário não autorizado. Refaça o login.");
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
