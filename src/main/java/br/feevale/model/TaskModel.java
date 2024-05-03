package br.feevale.model;

import br.feevale.enums.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity(name = "task")
public class TaskModel extends DefaultModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_task", nullable = false)
	private Long id;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	private UserModel patient;

	@Column(nullable = false, name = "owner_id_user")
	private Long ownerId;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String description;

	private int qtyStars;

	private boolean lostStarDoNotDo;

	private boolean lostStarDelay;

	private boolean hasAchievement;

	@Column(nullable = false, name = "achievement_id_achievement")
	private Long achievementId;

	@Transient
	private AchievementModel achievement;

	@Column(nullable = false)
	private Date dateToStart;

	@Column(nullable = false)
	private Date timeToStart;

	private int timeToDo;

	@Transient
	private String timeToDoFormated;

	private Date timeStart;

	private Date timeFinish;

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	private TaskStatus status;

}
