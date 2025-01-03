package br.feevale.model;

import br.feevale.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskModel {

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

	@Column(name = "achievement_id_achievement")
	private Long achievementId;

	@Transient
	private AchievementModel achievement;

	@Column(nullable = false)
	private String dateToStart;

	@Column(nullable = false)
	private String timeToStart;

	private String timeToDo;

	@Transient
	private String timeToDoFormatted;

	private Long currentDuration;

	@Transient
	private String currentDurationFormated;

	private Date timeStart;

	private Date timePlay;

	private Date timeFinish;

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	private TaskStatus status;

	@Transient
	private Date dateToStartDate;

	@Column(name = "dt_create")
	private Date dtCreate;

	@Column(name = "dt_update")
	private Date dtUpdate;

}
