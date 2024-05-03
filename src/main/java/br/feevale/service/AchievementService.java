package br.feevale.service;

import br.feevale.enums.AchievementStatus;
import br.feevale.exceptions.CustomException;
import br.feevale.model.AchievementModel;
import br.feevale.model.UserModel;
import br.feevale.repository.AchievementRepository;
import br.feevale.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class AchievementService {

	@Autowired
	private AchievementRepository repository;

	public AchievementModel save(AchievementModel model) {
		if (model.getId() == null) {
			return saveNew(model);
		} else {
			return update(model);
		}
	}

	private AchievementModel saveNew(AchievementModel model) {
		model.setDtCreate(new Date());
		model.setDtUpdate(new Date());
		return repository.save(model);
	}

	private AchievementModel update(AchievementModel model) {
		model.setDtUpdate(new Date());
		return repository.save(model);
	}

	public AchievementModel findById(long achievementId, UserModel loggedUser) {
		Optional<AchievementModel> model = repository.findById(achievementId);
		if (model.isPresent()) {
			if (UserUtils.isPatient(loggedUser)) {
				if (model.get().getPatientId().equals(loggedUser.getId())) {
					return model.get();
				}
			} else {
				return model.get();
			}
		}
		throw new CustomException("Conquista não localizada");
	}

	public AchievementModel findByIdWithoutValidation(long achievementId) {
		Optional<AchievementModel> model = repository.findById(achievementId);
		return model.orElse(null);
	}

	public List<AchievementModel> findAllByPatient(long idPatient, boolean loggedUserIsPatient) {
		if (loggedUserIsPatient) {
			return repository.findToPatientConquered(idPatient, AchievementStatus.CONQUERED.getOrdinal());
		} else {
			return repository.findByPatientId(idPatient);
		}
	}

	public List<AchievementModel> findAvailableToPatient(long idPatient) {
		return repository.findAvailableToPatient(idPatient);
	}
}
