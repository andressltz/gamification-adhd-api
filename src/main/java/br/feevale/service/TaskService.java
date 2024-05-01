package br.feevale.service;

import br.feevale.enums.TaskStatus;
import br.feevale.exceptions.CustomException;
import br.feevale.model.TaskModel;
import br.feevale.model.UserModel;
import br.feevale.repository.TaskRepository;
import br.feevale.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
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
		task.setStatus(TaskStatus.DO_NOT_STARTED);
		task.setDtCreate(new Date());
		task.setDtUpdate(new Date());
		return repository.save(task);
	}

	private TaskModel update(TaskModel task) {
		task.setDtUpdate(new Date());
		return repository.save(task);
	}

	public TaskModel findById(long taskId, UserModel loggedUser) {
		Optional<TaskModel> task = repository.findById(taskId);
		if (task.isPresent()) {
			if (UserUtils.isPatient(loggedUser)) {
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
			return repository.findToPatient(idPatient, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		} else {
			return repository.findByPatientId(idPatient);
		}
	}

	public TaskModel startTask(long idTask, UserModel loggedUser) {
		TaskModel task = findById(idTask, loggedUser);
		if (!TaskStatus.DOING.equals(task.getStatus())) {
			task.setStatus(TaskStatus.DOING);
			task.setTimeStart(new Date());
		}
		task.setDtUpdate(new Date());
		task = repository.save(task);
		task.setPatient(null);
		return task;
	}

	public TaskModel stopTask(long idTask, UserModel loggedUser) {
		TaskModel task = findById(idTask, loggedUser);
		task.setStatus(TaskStatus.PAUSED);
		task.setDtUpdate(new Date());
		task = repository.save(task);
		task.setPatient(null);
		return task;
	}

	public TaskModel finishTask(long idTask, UserModel loggedUser) {
		TaskModel task = findById(idTask, loggedUser);
		task.setStatus(TaskStatus.FINISHED);
		task.setTimeFinish(new Date());
		task.setDtUpdate(new Date());
		task = repository.save(task);
		task.setPatient(null);
		return task;
	}
}
