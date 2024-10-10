package br.feevale.repository;

import br.feevale.model.SessionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SessionRepository extends JpaRepository<SessionModel, Long> {

	void deleteByToken(String token);

	SessionModel findByToken(String token);

	SessionModel getByToken(String token);

	@Query(nativeQuery = true, value = "SELECT * " +
		" FROM app_session " +
		" WHERE user_id_user = ?1")
	List<SessionModel> getByUserId(long idUser);
}
