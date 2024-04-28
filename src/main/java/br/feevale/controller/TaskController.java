package br.feevale.controller;

import br.feevale.core.DefaultResponse;
import br.feevale.dtos.UserDto;
import br.feevale.exceptions.CustomException;
import br.feevale.model.TaskModel;
import br.feevale.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController extends BaseController {

	@Autowired
	private TaskService taskService;

	@ResponseBody
	@PostMapping()
	public DefaultResponse<TaskModel> postNew(@RequestHeader HttpHeaders headers, @RequestBody TaskModel taskModel) {
		try {
			final UserDto loggedUser = getAuthUser(headers);
			taskModel.setOwnerId(loggedUser.getId());
			return new DefaultResponse<>(taskService.save(taskModel));
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		}
	}

	@ResponseBody
	@GetMapping()
	public DefaultResponse<List<TaskModel>> getAll(@RequestHeader HttpHeaders headers) {
		try {
			final UserDto loggedUser = getAuthUser(headers);
			if (loggedUser.isPatient()) {
				return new DefaultResponse<>(taskService.findAllByPatient(loggedUser.getId(), true));
			}
			return new DefaultResponse<>(new ArrayList<>());
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		}
	}

	@ResponseBody
	@GetMapping("/{idTask}")
	public DefaultResponse<TaskModel> getById(@RequestHeader HttpHeaders headers, @PathVariable long idTask) {
		try {
			final UserDto loggedUser = getAuthUser(headers);
			TaskModel taskModel = taskService.findById(idTask, loggedUser);
			taskModel.setPatient(null);
			return new DefaultResponse<>(taskModel);
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		}
	}

	@ResponseBody
	@PatchMapping("/{id}")
	public DefaultResponse<TaskModel> patchUpdate(@PathVariable long id, @RequestBody TaskModel task) {
		try {
			task.setId(id);
			return new DefaultResponse<>(taskService.save(task));
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		}
	}

//	@ResponseBody
//	@DeleteMapping("/{id}")
//	public DefaultResponse<TaskModel> deleteById(@PathVariable long id) {
//		try {
//			return new DefaultResponse<>(taskService.deleteById(id));
//		} catch (CustomException ex) {
//			return new DefaultResponse<>(ex);
//		}
//	}

	@ResponseBody
	@GetMapping("/user/{idPatient}")
	public DefaultResponse<List<TaskModel>> getTasksByPatient(@RequestHeader HttpHeaders headers, @PathVariable long idPatient) {
		try {
			final UserDto loggedUser = getAuthUser(headers);
			return new DefaultResponse<>(taskService.findAllByPatient(idPatient, loggedUser.isPatient()));
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		}
	}

	@ResponseBody
	@PostMapping("/{idTask}/start")
	public DefaultResponse<TaskModel> postTaskStart(@RequestHeader HttpHeaders headers, @PathVariable long idTask) {
		try {
			final UserDto loggedUser = getAuthUser(headers);
			return new DefaultResponse<>(taskService.startTask(idTask, loggedUser));
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		}
	}
}
