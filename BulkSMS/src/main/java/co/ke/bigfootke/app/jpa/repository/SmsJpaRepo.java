package co.ke.bigfootke.app.jpa.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import co.ke.bigfootke.app.jpa.entities.Sms;

public interface SmsJpaRepo extends JpaRepository<Sms, Long>, 
													PagingAndSortingRepository<Sms,Long>{

	public Sms findByDate(Date date);
	
	public Page<Sms> findAll(Pageable page);
	
	@Transactional
    Page<Sms> findByGroupsGroupIdOrderByDate(Long groupId, Pageable pageable);
	
	@Transactional
	List<Sms> findByGroupsGroupIdOrderByDate(Long groupId);
}
