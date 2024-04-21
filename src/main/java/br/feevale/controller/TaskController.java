package br.feevale.controller;

import br.feevale.core.DefaultResponse;
import br.feevale.exceptions.CustomException;
import br.feevale.model.TaskModel;
import br.feevale.model.UserModel;
import br.feevale.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController extends BaseController {

	@Autowired
	private TaskService taskService;

	@ResponseBody
	@PostMapping()
	public DefaultResponse<TaskModel> postNew(@RequestBody TaskModel taskModel) {
		try {
			return new DefaultResponse<>(taskService.save(taskModel));
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		}
	}

	@ResponseBody
	@GetMapping()
	public DefaultResponse<List<TaskModel>> getAll(@RequestHeader HttpHeaders headers) {
		try {
			final UserModel loggedUser = getAuthUser(headers);
			return new DefaultResponse<>(taskService.findAll());
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		}
	}

	@ResponseBody
	@GetMapping("/{id}")
	public DefaultResponse<TaskModel> getById(@PathVariable long id) {
		try {
			return new DefaultResponse<>(taskService.findById(id));
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

	@ResponseBody
	@DeleteMapping("/{id}")
	public DefaultResponse<TaskModel> deleteById(@PathVariable long id) {
		try {
			return new DefaultResponse<>(taskService.deleteById(id));
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		}
	}
}
