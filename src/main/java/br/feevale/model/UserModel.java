package br.feevale.model;

import br.feevale.enums.Gender;
import br.feevale.enums.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity(name = "app_user")
public class UserModel extends DefaultModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_user", nullable = false)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	private String image;

	private String phone;

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = true)
	private Gender gender;

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	private UserType type;

	@ManyToMany
	private List<UserModel> patients;

	@Transient
	private String phoneFormated;

	@Column
	private Integer qtyStars;

	@Column
	private Long totalDuration;

	@Transient
	private String totalDurationFormatted;

	@Column(nullable = true)
	private Integer level;

	@Transient
	private Integer maxLevel;

	@Transient
	private Integer maxStars;

}
