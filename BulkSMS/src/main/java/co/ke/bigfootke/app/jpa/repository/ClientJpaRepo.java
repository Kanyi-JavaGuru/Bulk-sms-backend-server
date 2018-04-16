package co.ke.bigfootke.app.jpa.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import co.ke.bigfootke.app.jpa.entities.Client;

public interface ClientJpaRepo extends JpaRepository<Client, Long>, PagingAndSortingRepository<Client,Long>{

	public Client findByPhoneNo(String phoneNo);
	
	public Page<Client> findAll(Pageable page);
	
	@Transactional
    Page<Client> findByGroupsGroupIdOrderByPhoneNo(Long groupId, Pageable pageable);
	
	@Transactional
	List<Client> findByGroupsGroupIdOrderByPhoneNo(Long groupId);
}
