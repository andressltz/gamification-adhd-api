package br.feevale.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DefaultModel {

	@Column(name = "dt_create")
	private Date dtCreate;

	@Column(name = "dt_update")
	private Date dtUpdate;

}
