package br.feevale.repository;

import br.feevale.model.SessionModel;
import br.feevale.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<SessionModel, Long> {

	void deleteByToken(String token);

	SessionModel findByToken(String token);

	SessionModel getByToken(String token);

	List<SessionModel> getByUser(UserModel user);
}
