package br.feevale.service;

import br.feevale.exceptions.CustomException;
import br.feevale.model.TaskModel;
import br.feevale.model.UserModel;
import br.feevale.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class TaskService {

	@Autowired
	private TaskRepository repository;

	public TaskModel save(TaskModel task) {
		if (task.getId() == null) {
			return saveNew(task);
		} else {
			return update(task);
		}
	}

	private TaskModel saveNew(TaskModel task) {
		task.setDtCreate(new Date());
		task.setDtUpdate(new Date());
		return repository.save(task);
	}

	private TaskModel update(TaskModel task) {
		task.setDtUpdate(new Date());
		return repository.save(task);
	}

	public TaskModel findById(long taskId, UserModel loggedUser, boolean loggedIsPatient) {
		Optional<TaskModel> task = repository.findById(taskId);
		if (task.isPresent()) {
			if (loggedIsPatient) {
				if (task.get().getPatient().getId().equals(loggedUser.getId())) {
					return task.get();
				}
			} else {
				return task.get();
			}
		}
		throw new CustomException("Tarefa não localizada");
	}

	public TaskModel deleteById(long id) {
		// Todo implement
		return null;
	}

	public List<TaskModel> findAll() {
		return repository.findAll();
	}

	public List<TaskModel> findAllByPatient(long idPatient, boolean loggedUserIsPatient) {
		if (loggedUserIsPatient) {
			return repository.findToPatient(idPatient);
		} else {
			return repository.findByPatientId(idPatient);
		}
	}
}
