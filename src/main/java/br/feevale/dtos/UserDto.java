package br.feevale.dtos;

import br.feevale.enums.UserType;
import br.feevale.model.UserModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class UserDto {

	public UserDto() {

	}

	public UserDto(UserModel model) {
		if (model != null) {
			this.id = model.getId();
			this.name = model.getName();
			this.email = model.getEmail();
			this.image = model.getImage();
			this.phone = model.getPhone();
			this.type = model.getType();
//			this.patients = model.getPatients();
//			this.phoneFormated = model.getPhone();
			this.dtCreate = model.getDtCreate();
			this.dtUpdate = model.getDtUpdate();
		}
		new UserDto();
	}

	private Long id;

	private String name;

	private String email;

	private String image;

	private String phone;

	private UserType type;

	private List<UserDto> patients;

	private String phoneFormated;

	private Date dtCreate;

	private Date dtUpdate;

	public boolean isPatient() {
		return UserType.PATIENT.equals(this.type);
	}

	public boolean isNotPatient() {
		return !UserType.PATIENT.equals(this.type);
	}

	public UserModel toModel() {
		UserModel model = new UserModel();
		model.setId(this.id);
//		this.id = model.getId();
//		this.name = model.getName();
//		this.email = model.getEmail();
//		this.image = model.getImage();
//		this.phone = model.getPhone();
//		this.type = model.getType();
////			this.patients = model.getPatients();
////			this.phoneFormated = model.getPhone();
//		this.dtCreate = model.getDtCreate();
//		this.dtUpdate = model.getDtUpdate();
		return model;
	}
}
