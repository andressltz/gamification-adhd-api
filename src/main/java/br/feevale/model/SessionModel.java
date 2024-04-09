package br.feevale.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity(name = "app_session")
public class SessionModel extends DefaultModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_session", nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(nullable = false)
	private UserModel user;

	@Column(nullable = false)
	private String token;

	@Column(nullable = false)
	private Date expiration;

}
