package br.feevale.repository;

import br.feevale.enums.AchievementStatus;
import br.feevale.model.AchievementModel;
import br.feevale.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AchievementRepository extends JpaRepository<AchievementModel, Long> {

	List<AchievementModel> findByPatient(UserModel patient);

	List<AchievementModel> findByPatientId(long idPatient);

	@Query(nativeQuery = true, value = "SELECT * " +
		" FROM achievement" +
		" WHERE patient_id_user = ?1" +
		" AND status = ?2")
	List<AchievementModel> findToPatientConquered(long idPatient, AchievementStatus status);

	@Query(nativeQuery = true, value = "SELECT * " +
		" FROM achievement" +
		" WHERE patient_id_user = ?1")
	List<AchievementModel> findAvailableToPatient(long idPatient);
}
