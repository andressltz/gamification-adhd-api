package br.feevale.controller;

import br.feevale.core.DefaultResponse;
import br.feevale.enums.AchievementStatus;
import br.feevale.exceptions.CustomException;
import br.feevale.model.AchievementModel;
import br.feevale.model.UserModel;
import br.feevale.service.AchievementService;
import br.feevale.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/achievement")
public class AchievementController extends BaseController {

	@Autowired
	private AchievementService taskService;

	@ResponseBody
	@PostMapping()
	public DefaultResponse<AchievementModel> postNew(@RequestHeader HttpHeaders headers, @RequestBody AchievementModel model) {
		try {
			final UserModel loggedUser = getAuthUser(headers);
			model.setOwnerId(loggedUser.getId());
			model.setStatus(AchievementStatus.DO_NOT_CONQUERED);
			return new DefaultResponse<>(taskService.save(model));
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		}
	}

	@ResponseBody
	@GetMapping()
	public DefaultResponse<List<AchievementModel>> getAll(@RequestHeader HttpHeaders headers) {
		try {
			final UserModel loggedUser = getAuthUser(headers);
			if (UserUtils.isPatient(loggedUser)) {
				return new DefaultResponse<>(taskService.findAllByPatient(loggedUser.getId(), true));
			}
			return new DefaultResponse<>(new ArrayList<>());
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		}
	}

//	@ResponseBody
//	@GetMapping("/{idAchievement}")
//	public DefaultResponse<AchievementModel> getById(@RequestHeader HttpHeaders headers, @PathVariable long idAchievement) {
//		try {
//			final UserModel loggedUser = getAuthUser(headers);
//			AchievementModel taskModel = taskService.findById(idAchievement, loggedUser);
//			taskModel.setPatient(null);
//			return new DefaultResponse<>(taskModel);
//		} catch (CustomException ex) {
//			return new DefaultResponse<>(ex);
//		}
//	}

//	@ResponseBody
//	@PatchMapping("/{id}")
//	public DefaultResponse<AchievementModel> patchUpdate(@RequestHeader HttpHeaders headers, @PathVariable long id, @RequestBody AchievementModel model) {
//		try {
//			final UserModel loggedUser = getAuthUser(headers);
//			model.setId(id);
//			model.setOwnerId(loggedUser.getId());
//			return new DefaultResponse<>(taskService.save(model));
//		} catch (CustomException ex) {
//			return new DefaultResponse<>(ex);
//		}
//	}

	@ResponseBody
	@GetMapping("/user/{idPatient}")
	public DefaultResponse<List<AchievementModel>> getTasksByPatient(@RequestHeader HttpHeaders headers, @PathVariable long idPatient) {
		try {
			final UserModel loggedUser = getAuthUser(headers);
			return new DefaultResponse<>(taskService.findAllByPatient(idPatient, UserUtils.isPatient(loggedUser)));
		} catch (CustomException ex) {
			return new DefaultResponse<>(ex);
		}
	}

}
