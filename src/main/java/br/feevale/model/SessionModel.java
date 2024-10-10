package br.feevale.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_session", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	private UserModel user;

	@Column(nullable = false)
	private String token;

	@Column(nullable = false)
	private Date expiration;

	@Column
	private String agent;

	@Column(name = "dt_create")
	private Date dtCreate;

	@Column(name = "dt_update")
	private Date dtUpdate;
}
