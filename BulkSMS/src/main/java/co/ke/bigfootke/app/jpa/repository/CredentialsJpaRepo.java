package co.ke.bigfootke.app.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.ke.bigfootke.app.jpa.entities.Credentials;

public interface CredentialsJpaRepo extends JpaRepository<Credentials, Long>{
	public Credentials findByUserUserId(Long userId);
}
