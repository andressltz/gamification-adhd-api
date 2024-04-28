package br.feevale.service;

import br.feevale.dtos.UserDto;
import br.feevale.model.TaskModel;

import java.util.List;

public interface TaskService {

	TaskModel save(TaskModel task);

	List<TaskModel> findAllByPatient(long idPatient, boolean loggedUserIsPatient);

	TaskModel findById(long taskId, UserDto loggedUser);

	TaskModel startTask(long idTask, UserDto loggedUser);
}
