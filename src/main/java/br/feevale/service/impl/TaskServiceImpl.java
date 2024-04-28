package br.feevale.service.impl;

import br.feevale.dtos.UserDto;
import br.feevale.enums.TaskStatus;
import br.feevale.exceptions.CustomException;
import br.feevale.model.TaskModel;
import br.feevale.repository.TaskRepository;
import br.feevale.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class TaskServiceImpl implements TaskService {

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

	public TaskModel findById(long taskId, UserDto loggedUser) {
		Optional<TaskModel> task = repository.findById(taskId);
		if (task.isPresent()) {
			if (loggedUser.isPatient()) {
				if (task.get().getPatient().getId().equals(loggedUser.getId())) {
					return task.get();
				}
			} else {
				return task.get();
			}
		}
		throw new CustomException("Tarefa não localizada");
	}

//	public TaskModel deleteById(long id) {
//		// Todo implement
//		return null;
//	}

	public List<TaskModel> findAllByPatient(long idPatient, boolean loggedUserIsPatient) {
		if (loggedUserIsPatient) {
			return repository.findToPatient(idPatient);
		} else {
			return repository.findByPatientId(idPatient);
		}
	}

	public TaskModel startTask(long idTask, UserDto loggedUser) {
		TaskModel task = findById(idTask, loggedUser);
		task.setStatus(TaskStatus.DOING);
		task.setDtUpdate(new Date());
		return task;
	}
}
