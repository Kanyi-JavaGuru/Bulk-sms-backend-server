package co.ke.bigfootke.app.jpa.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import co.ke.bigfootke.app.jpa.entities.ScheduledSms;
import co.ke.bigfootke.app.jpa.implementations.GroupJpaImplementation;
import co.ke.bigfootke.app.jpa.implementations.ScheduleJpaImplementation;
import co.ke.bigfootke.app.pojos.ScheduledSmsContainer;

@Service
public class ScheduleJpaService {

	@Autowired
	private ScheduleJpaImplementation repository;
	@Autowired
	private GroupJpaImplementation groupImpl;
	private Map<String, String> response;
	
 	public ResponseEntity<Object> create(ScheduledSmsContainer smsContainer  ) {
		response = new HashMap<>();
		ScheduledSms schedule = new ScheduledSms(smsContainer.getTitle(), smsContainer.getMessage(), smsContainer.getDate(),
				smsContainer.getTime());
		if(schedule.getMessage() != null && schedule.getTitle() != null) {
			ScheduledSms newSchedule = repository.create(schedule);
			response.put("message", "Success: Created "+newSchedule);
			//retrieve groups ids to add to schedule
			List<Long> groupIds = smsContainer.getGroupIds();
			//add groups to schedule
			groupImpl.addToSchedule(newSchedule.getScheduleId(), groupIds);
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		response.put("message", "Error: Fill required fields");
		return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
	}
	
	@Cacheable(value = "schedule.id", key="#scheduleId", unless = "#result != null")
	public ResponseEntity<Object> findById(Long scheduleId) {
		if(!repository.exists(scheduleId)) {
			response = new HashMap<>();
			response.put("message", "Error: Schedule not found");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Object>(repository.findById(scheduleId), HttpStatus.OK);
	}
	
	@Cacheable(value = "schedule.paged", key="#pageNo", unless = "#result != null")
	public ResponseEntity<Page<ScheduledSms>> findAll(int pageNo, int pageSize) {
		return repository.findAll(pageNo, pageSize);
	}
	
	@Cacheable(value = "schedule.title", key="#title", unless = "#result != null")
	public ResponseEntity<Object> findByTitle(String title) {
		return new ResponseEntity<Object>(repository.findByTitle(title), HttpStatus.OK);
	}
	
	public ResponseEntity<Object> findBtwnDates(Date firstDate, Date lastDate) {
		return new ResponseEntity<Object>(repository.findBtwnDates(firstDate, lastDate), HttpStatus.OK);
	}
	
	@Cacheable(value = "schedule.date", key="#title", unless = "#result != null")
	public ResponseEntity<Object> findByDate(Date date) {
		return new ResponseEntity<Object>(repository.findByDate(date), HttpStatus.OK);
	}
	
	@Cacheable(value = "schedule.byGroup", key="#groupId", unless = "#result != null")
	public ResponseEntity<Object> findByGroup(Long groupId, int pageNo, int pageSize) {
		if(!groupImpl.exists(groupId)) {
			response.put("message", "Error: Could not find group");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);				
		}
		final Page<ScheduledSms> schedule = repository.findByGroup(groupId, pageNo, pageSize);
		return new ResponseEntity<Object>(schedule, HttpStatus.OK);
	} 
	
	@Caching(evict = {@CacheEvict(value = "schedule.paged", key = "#scheduleId"),
			@CacheEvict(value = "schedule.id", key = "#scheduleId"),
			@CacheEvict(value = "schedule.date", key = "#scheduleId"),
			@CacheEvict(value = "schedule.title", key = "#scheduleId"),
			@CacheEvict(value = "schedule.byGroup", key = "#scheduleId")})
	public ResponseEntity<Object> delete(Long scheduleId) {
		if(!repository.exists(scheduleId)) {
			response = new HashMap<>();
			response.put("message", "Error: Schedule not found");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);			
		}
		response.put("message", "Deleted Schedule");
		repository.delete(scheduleId);
		return new ResponseEntity<Object>(response, HttpStatus.OK);	
	}

		
}
