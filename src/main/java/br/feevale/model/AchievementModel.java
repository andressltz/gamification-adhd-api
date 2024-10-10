package br.feevale.model;

import br.feevale.enums.AchievementStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity(name = "achievement")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AchievementModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_achievement", nullable = false)
	private Long id;

	@Column(nullable = false, name = "patient_id_user")
	private Long patientId;

	@Column(nullable = false, name = "owner_id_user")
	private Long ownerId;

	@Column(nullable = false)
	private String title;

	private Date dateConquered;

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	private AchievementStatus status;

	private String image;

	@Column(name = "dt_create")
	private Date dtCreate;

	@Column(name = "dt_update")
	private Date dtUpdate;

}
