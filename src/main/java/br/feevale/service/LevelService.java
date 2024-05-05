package br.feevale.service;

import br.feevale.model.TaskModel;
import br.feevale.model.UserModel;
import br.feevale.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LevelService {

	private static final int MAX_LEVEL = 30;

	private static final int STARS_TO_NEXT_LEVEL = 50;

	@Autowired
	private TaskRepository repository;

	@Autowired
	private AchievementService achievementService;

	@Autowired
	private UserService userService;

	public void setUserLevel(UserModel patient) {
		int currentLevel = patient.getLevel();
		if (currentLevel != MAX_LEVEL) {
			int currentStars = patient.getQtyStars();

			int needStarsNextLevel = currentLevel * STARS_TO_NEXT_LEVEL;
			if (currentStars >= needStarsNextLevel) {
				currentLevel = currentLevel + 1;
				patient.setLevel(currentLevel);
			}
		}
	}

	public UserModel calculateStars(boolean lostStarDelay, boolean lostStarDoNotDo, int qtyStarsTask, UserModel pacient) {
		if (lostStarDelay) {
//			if (task.getTimeToDo() > 0 && task.getCurrentDuration() != null && task.getCurrentDuration() > task.getTimeToDo()) {
//				userService.lostStars(task.getPatient(), task.getQtyStars());
//			} else {
//				userService.addStars(task.getPatient(), task.getQtyStars());
//			}
		} else {
			int totalStarts = userService.sumStars(pacient, qtyStarsTask);
			pacient.setQtyStars(totalStarts);
		}
		return pacient;
	}

}
