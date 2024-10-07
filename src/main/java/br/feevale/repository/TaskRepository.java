package br.feevale.repository;

import br.feevale.model.TaskModel;
import br.feevale.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskModel, Long> {

	List<TaskModel> findByPatient(UserModel patient);

	List<TaskModel> findByPatientId(long idPatient);

	@Query(nativeQuery = true, value = "SELECT * " +
		" FROM task" +
		" WHERE patient_id_user = ?1" +
		" ORDER BY date_to_start")
	List<TaskModel> findToPatient(long idPatient);
}
