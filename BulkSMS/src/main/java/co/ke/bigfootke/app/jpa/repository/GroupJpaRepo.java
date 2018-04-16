package co.ke.bigfootke.app.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.ke.bigfootke.app.jpa.entities.Group;

public interface GroupJpaRepo extends JpaRepository<Group, Long>{
	
	public Group findByName(String name);
}
