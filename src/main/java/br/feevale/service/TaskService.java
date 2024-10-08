package br.feevale.service;

import br.feevale.enums.TaskStatus;
import br.feevale.exceptions.CustomException;
import br.feevale.exceptions.ValidationException;
import br.feevale.model.TaskModel;
import br.feevale.model.UserModel;
import br.feevale.repository.TaskRepository;
import br.feevale.utils.CustomStringUtils;
import br.feevale.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class TaskService {

	private static final Logger LOG = LogManager.getLogger();

	@Autowired
	private TaskRepository repository;

	@Autowired
	private AchievementService achievementService;

	@Autowired
	private UserService userService;

	@Autowired
	private LevelService levelService;

	public TaskModel save(TaskModel task) {
		try {
			task.setTimeToDo(CustomStringUtils.numberOrNull(task.getTimeToDo()));
			validateTask(task);

			task.setTimeToStart(task.getDateToStart());

			if (task.getId() == null) {
				TaskModel savedTask = saveNew(task);
				patientCleaner(savedTask);
				return savedTask;
			} else {
				TaskModel savedTask = update(task);
				patientCleaner(savedTask);
				return savedTask;
			}
		} catch (ValidationException ex) {
			LOG.error(ex.getMessage(), ex);
			throw new CustomException(ex.getMessage(), ex);
		} catch (Exception ex) {
			LOG.error("Erro ao salvar", ex);
			throw new CustomException("Erro ao salvar.", ex);
		}
	}

	private void patientCleaner(TaskModel savedTask) {
		savedTask.getPatient().setPassword(null);
		savedTask.getPatient().setPatients(null);
		savedTask.getPatient().setPhone(null);
		savedTask.getPatient().setEmail(null);
		savedTask.getPatient().setLoginUser(null);
		savedTask.getPatient().setProfiles(null);
	}

	private void validateTask(@NotNull TaskModel task) throws CustomException {
		if (StringUtils.isEmpty(task.getTitle()) || StringUtils.isEmpty(task.getTitle().trim())) {
			throw new ValidationException("Título deve ser preenchido.");
		}

		if (StringUtils.isEmpty(task.getDescription()) || StringUtils.isEmpty(task.getDescription().trim())) {
			throw new ValidationException("Orientações devem ser preenchidas.");
		}

		if (task.getDateToStart() == null || CustomStringUtils.numberOrNull(task.getDateToStart()) == null) {
			throw new ValidationException("Data inicial inválida.");
		}

		if (task.getDateToStart().length() == 8) {
			task.setDateToStart(task.getDateToStart() + "0000");
		}

		if (task.getDateToStart().length() != 12) {
			throw new ValidationException("Data inicial inválida.");
		}

		if (task.getTimeToDo() != null && !task.getTimeToDo().isEmpty() && task.getTimeToDo().length() != 4) {
			throw new ValidationException("Tempo para realização inválido.");
		}
	}

	private TaskModel saveNew(TaskModel task) {
		task.setStatus(TaskStatus.DO_NOT_STARTED);
		task.setDtCreate(new Date());
		task.setDtUpdate(new Date());
		return repository.save(task);
	}

	private TaskModel update(TaskModel task) {
		task.setDtUpdate(new Date());
		return repository.save(task);
	}

	public TaskModel findById(long taskId, UserModel loggedUser) {
		Optional<TaskModel> task = repository.findById(taskId);
		if (task.isPresent()) {
			if (UserUtils.isPatient(loggedUser)) {
				if (task.get().getPatient().getId().equals(loggedUser.getId())) {
					return task.get();
				}
			} else {
				return task.get();
			}
		}
		throw new CustomException("Tarefa não localizada");
	}

	public TaskModel findByIdWithAchievement(long taskId, UserModel loggedUser) {
		Optional<TaskModel> task = repository.findById(taskId);
		if (task.isPresent()) {
			if (UserUtils.isPatient(loggedUser)) {
				if (task.get().getPatient().getId().equals(loggedUser.getId())) {
					TaskModel model = task.get();
					if (model.isHasAchievement() && model.getAchievementId() != null) {
						model.setAchievement(achievementService.findByIdWithoutValidation(model.getAchievementId()));
					}
					return model;
				}
			} else {
				return task.get();
			}
		}
		throw new CustomException("Tarefa não localizada");
	}

	public List<TaskModel> findAllByPatient(long idPatient, boolean loggedUserIsPatient) {
		List<TaskModel> listTasks;
		if (loggedUserIsPatient) {
			listTasks = repository.findToPatient(idPatient);
			List<TaskModel> newListTasks = new ArrayList<>();
			for (TaskModel task : listTasks) {
				Date dateToStart = CustomStringUtils.stringToDate(task.getDateToStart(), CustomStringUtils.DDMMYYYYHHMM);
				if (dateToStart != null && !dateToStart.after(new Date())) {
					task.setPatient(null);
					task.setTimeToDoFormatted(CustomStringUtils.getDurationFormatted(task.getTimeToDo()));
					task.setDateToStartDate(dateToStart);
					if (task.isHasAchievement() && task.getAchievementId() != null) {
						task.setAchievement(achievementService.findByIdWithoutValidation(task.getAchievementId()));
					}
					newListTasks.add(task);
				}
			}
			return newListTasks;
		} else {
			listTasks = repository.findByPatientId(idPatient);
			for (TaskModel task : listTasks) {
				task.setPatient(null);
				Date dateToStart = CustomStringUtils.stringToDate(task.getDateToStart(), CustomStringUtils.DDMMYYYYHHMM);
				if (dateToStart != null && dateToStart.after(new Date())) {
					task.setStatus(TaskStatus.BLOCKED);
				}
				task.setDateToStartDate(dateToStart);
				task.setTimeToDoFormatted(CustomStringUtils.getDurationFormatted(task.getTimeToDo()));
			}
		}
		return listTasks;
	}

	public TaskModel startTask(long idTask, UserModel loggedUser) {
		TaskModel task = findById(idTask, loggedUser);
		if (!TaskStatus.DOING.equals(task.getStatus())) {
			task.setStatus(TaskStatus.DOING);
			task.setTimeStart(task.getTimeToStart() == null ? new Date() : CustomStringUtils.stringToDate(task.getTimeToStart(), CustomStringUtils.DDMMYYYYHHMM));
			task.setTimePlay(task.getTimePlay() == null ? new Date() : task.getTimePlay());
			task.setCurrentDuration(task.getCurrentDuration() == null ? 0L : task.getCurrentDuration());
		}
		task.setDtUpdate(new Date());
		task = repository.save(task);
		task.setPatient(null);
		return task;
	}

	public TaskModel playTask(long idTask, UserModel loggedUser) {
		TaskModel task = findById(idTask, loggedUser);
		task.setStatus(TaskStatus.DOING);
		task.setTimePlay(new Date());
		task.setDtUpdate(new Date());

		task = repository.save(task);
		task.setPatient(null);
		return task;
	}

	public TaskModel pauseTask(long idTask, UserModel loggedUser) {
		TaskModel task = findById(idTask, loggedUser);
		task.setStatus(TaskStatus.PAUSED);
		task.setCurrentDuration(calculateDuration(task));
		task.setTimePlay(null);
		task.setDtUpdate(new Date());

		task = repository.save(task);
		task.setPatient(null);
		return task;
	}

	public TaskModel stopTask(long idTask, UserModel loggedUser) {
		TaskModel task = findById(idTask, loggedUser);
		task.setStatus(TaskStatus.PAUSED);
		task.setCurrentDuration(calculateDuration(task));
		task.setTimePlay(null);
		task.setDtUpdate(new Date());

		task = repository.save(task);
		task.setPatient(null);
		return task;
	}

	public TaskModel finishTask(long idTask, UserModel loggedUser) {
		TaskModel task = findById(idTask, loggedUser);

		task.setStatus(TaskStatus.FINISHED);
		task.setDtUpdate(new Date());

		if (task.getTimeFinish() == null) {
			task.setTimeFinish(new Date());
			task.setCurrentDuration(calculateDuration(task));
			task.setTimePlay(null);
			task = repository.save(task);

			achievementService.setConquered(task.isHasAchievement(), task.getAchievementId());
			if (task.getPatient() != null && task.getPatient().getId() != null) {
				UserModel patientModel = task.getPatient();
				if (task.getQtyStars() > 0) {
					levelService.calculateStars(task.isLostStarDelay(), task.isLostStarDoNotDo(), task.getQtyStars(), patientModel, task.getTimeToDo(), task.getCurrentDuration());
					levelService.setUserLevel(patientModel);
				}
				patientModel.setTotalDuration(patientModel.getTotalDuration() == null ? task.getCurrentDuration() : patientModel.getTotalDuration() + task.getCurrentDuration());

				userService.save(patientModel, false, false);
			}
		} else {
			task = repository.save(task);
		}

		task.setPatient(null);
		return task;
	}

	private Long calculateDuration(TaskModel task) {
		if (task.getTimeStart() != null && task.getCurrentDuration() == null) {
			Duration duration = Duration.between(task.getTimeStart().toInstant(), new Date().toInstant());
			return duration.toMinutes();
		} else if (task.getTimePlay() != null && task.getCurrentDuration() != null) {
			Duration currentDuration = Duration.between(task.getTimePlay().toInstant(), new Date().toInstant());
			return currentDuration.toMinutes() + task.getCurrentDuration();

		} else if (task.getTimePlay() == null && task.getCurrentDuration() != null) {
			return task.getCurrentDuration();
		}
		return 0L;
	}
}
