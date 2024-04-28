package br.feevale.dtos;

import br.feevale.enums.TaskStatus;
import br.feevale.model.UserModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TaskDto {

	private Long id;

	private UserDto patient;

	private Long ownerId;

	private String title;

	private String description;

	private int qtyStars;

	private boolean lostStarDoNotDo;

	private boolean lostStarDelay;

	private boolean hasAchievement;

//	private AchievementModel achievement;

	private Date dateToStart;

	private Date timeToStart;

	private int timeToDo;

	private Date timeStart;

	private Date timeFinish;

	private TaskStatus status;

	private Date dtCreate;

	private Date dtUpdate;
}
