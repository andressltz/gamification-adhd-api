package br.feevale.repository;

import br.feevale.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserModel, Long> {

	UserModel findByEmail(String email);

	UserModel findByEmailAndPassword(String email, String encryptPassword);
}
