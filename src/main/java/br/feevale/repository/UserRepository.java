package br.feevale.repository;

import br.feevale.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserModel, Long> {

	UserModel findByEmail(String email);

	UserModel findByEmailAndPassword(String email, String encryptPassword);

	List<UserModel> findByPhone(String phone);
}
