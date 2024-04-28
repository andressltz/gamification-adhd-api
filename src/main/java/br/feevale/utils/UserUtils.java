package br.feevale.utils;

import br.feevale.enums.UserType;
import br.feevale.model.UserModel;

public class UserUtils {

	private UserUtils() {
	}

	public static boolean isNotPatient(UserModel userModel) {
		return !UserType.PATIENT.equals(userModel.getType());
	}

	public static boolean isPatient(UserModel userModel) {
		return UserType.PATIENT.equals(userModel.getType());
	}

}
