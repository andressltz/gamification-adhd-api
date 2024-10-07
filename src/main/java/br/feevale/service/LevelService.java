package br.feevale.service;

import br.feevale.model.UserModel;
import br.feevale.repository.TaskRepository;
import br.feevale.utils.CustomStringUtils;
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
		int currentLevel = patient.getLevel() == null || patient.getLevel() < 1 ? 1 : patient.getLevel();
		if (currentLevel != MAX_LEVEL) {
			int currentStars = patient.getQtyStars();

			int needStarsNextLevel = getMaxStarsToThisLevel(currentLevel);
			if (currentStars >= needStarsNextLevel) {
				currentLevel = currentLevel + 1;
				patient.setLevel(currentLevel);
			}
		}
	}

	public void calculateStars(boolean lostStarDelay, boolean lostStarDoNotDo, int qtyStarsTask, UserModel patient, String taskTimeToDoString, Long taskCurrentDuration) {
		if (lostStarDoNotDo) {
			//
		}

		int taskTimeToDo = CustomStringUtils.numberOrZero(taskTimeToDoString);
		if (lostStarDelay && taskTimeToDo > 0 && taskCurrentDuration != null && taskCurrentDuration > taskTimeToDo) {
			int totalStarts = userService.lostStars(patient.getQtyStars(), qtyStarsTask);
			patient.setQtyStars(totalStarts);
		} else {
			int totalStarts = userService.sumStars(patient.getQtyStars(), qtyStarsTask);
			patient.setQtyStars(totalStarts);
		}
	}

	public void setMaxLevelAndMaxStars(UserModel user) {
		user.setMaxLevel(MAX_LEVEL);
		user.setMaxStars(getMaxStarsToThisLevel(user.getLevel()));
	}

	private int getMaxStarsToThisLevel(Integer currentLevel) {
		if (currentLevel == null || currentLevel < 1) {
			currentLevel = 1;
		}
		return currentLevel * STARS_TO_NEXT_LEVEL;
	}
}
