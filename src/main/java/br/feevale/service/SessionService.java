package br.feevale.service;

import br.feevale.enums.UserType;
import br.feevale.exceptions.CustomException;
import br.feevale.exceptions.UnauthorizedException;
import br.feevale.model.SessionModel;
import br.feevale.model.UserModel;
import br.feevale.repository.SessionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
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

	public SessionModel login(UserModel userParam, String agent) {
		if (userParam != null && userParam.getEmail() != null && userParam.getPassword() != null) {
			UserModel userRes = userService.findByEmailAndPassword(userParam.getEmail(), userParam.getPassword());
			if (userRes != null) {
				return authorizeAndReturnSessionWithCleanUser(agent, userRes);
			}
		}

		throw new CustomException("E-mail ou senha inválidos");
	}

	public SessionModel loginProfile(UserModel userParam, Long loggedUserId, String agent) {
		if (userParam != null && userParam.getId() != null) {
			UserModel userRes = userService.getUser(userParam.getId());
			if (userRes != null && UserType.PATIENT.equals(userRes.getType()) && userRes.getLoginUser() != null && loggedUserId.equals(userRes.getLoginUser().getId())) {
				return authorizeAndReturnSessionWithCleanUser(agent, userRes);
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

	private SessionModel authorize(Long idUser, String agent) {
		if (idUser == null) {
			return null;
		}

		List<SessionModel> sessions = repository.getByUserId(idUser);
		if (sessions != null && !sessions.isEmpty()) {
			return sessions.stream().findFirst().get();
		}
		return createNewSession(idUser, agent);
	}

	private SessionModel createNewSession(long userId, String agent) {
		UserModel user = userService.findByIdInternal(userId);
		SessionModel newSession = new SessionModel();
		newSession.setUser(user);
		newSession.setToken(UUID.randomUUID().toString());
		newSession.setExpiration(Date.from(LocalDateTime.now().plusDays(90).toInstant(ZoneOffset.of("-03:00"))));
		newSession.setDtCreate(new Date());
		newSession.setDtUpdate(new Date());
		newSession.setAgent(agent);

		newSession = repository.save(newSession);

		user.setPassword(null);
		user.setLoginUser(null);
		user.setPatients(null);

		newSession.setUser(user);
		newSession.setId(null);
		return newSession;
	}

	@Nullable
	private SessionModel authorizeAndReturnSessionWithCleanUser(String agent, UserModel userRes) {
		SessionModel userSession = authorize(userRes.getId(), agent);
		if (userSession != null) {
			userRes.setPassword(null);
			userRes.setLoginUser(null);
			userRes.setPatients(null);
			userSession.setUser(userRes);
			userSession.setId(null);
		}
		return userSession;
	}

}
