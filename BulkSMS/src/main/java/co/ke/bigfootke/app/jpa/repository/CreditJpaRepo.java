package co.ke.bigfootke.app.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.ke.bigfootke.app.jpa.entities.Credit;

public interface CreditJpaRepo extends JpaRepository<Credit, Long>{
}
