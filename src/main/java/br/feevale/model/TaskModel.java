package br.feevale.model;

import br.feevale.enums.TaskStatus;
import br.feevale.enums.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity(name = "task")
public class TaskModel extends DefaultModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idTask", nullable = false)
	private Long id;

	@OneToOne
	@JoinColumn(nullable = false)
	private UserModel patient;

	@OneToOne
	@JoinColumn(nullable = false)
	private UserModel owner;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String description;

	private int qtyStars;

	private boolean lostStarDoNotDo;

	private boolean lostStarDelay;

	private boolean hasAchievement;

//	private AchievementModel achievement;

	@Column(nullable = false)
	private Date dateToStart;

	@Column(nullable = false)
	private Date timeToStart;

	private int timeToDo;

	private Date timeStart;

	private Date timeFinish;

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	private TaskStatus status;

}
