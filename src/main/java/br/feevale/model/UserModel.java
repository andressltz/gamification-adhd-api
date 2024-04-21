package br.feevale.model;

import br.feevale.enums.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
	@Column(nullable = false)
	private UserType type;

	@OneToMany
	private List<UserModel> patients;

}