package br.feevale.service;

import br.feevale.model.TaskModel;
import br.feevale.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

	public TaskModel findById(long taskId) {
		return repository.getReferenceById(taskId);
	}

	public TaskModel deleteById(long id) {
		// Todo implement
		return null;
	}

	public List<TaskModel> findAll() {
		return repository.findAll();
	}
}
