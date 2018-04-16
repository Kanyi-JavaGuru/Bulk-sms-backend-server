package co.ke.bigfootke.app.jpa.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import co.ke.bigfootke.app.jpa.entities.ScheduledSms;

public interface ScheduleJpaRepo extends JpaRepository<ScheduledSms, Long>, 
											PagingAndSortingRepository<ScheduledSms,Long>{

	public ScheduledSms findByTitle(String title);
	
	public ScheduledSms findByDate(Date date);
	
	public Page<ScheduledSms> findAll(Pageable page);
	
	@Transactional
    Page<ScheduledSms> findByGroupsGroupIdOrderByDate(Long groupId, Pageable pageable);
	
	@Transactional
	List<ScheduledSms> findByGroupsGroupIdOrderByDate(Long groupId);
}
