package co.ke.bigfootke.app.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.ke.bigfootke.app.jpa.entities.User;

public interface UserJpaRepo extends JpaRepository<User, Long>{

	public User findByEmail(String email);
}
