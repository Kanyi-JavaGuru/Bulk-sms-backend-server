package co.ke.bigfootke.app.jpa.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import co.ke.bigfootke.app.africastalking.SmsDispatcher;
import co.ke.bigfootke.app.jpa.entities.Group;
import co.ke.bigfootke.app.jpa.entities.Sms;
import co.ke.bigfootke.app.jpa.implementations.ClientJpaImplementation;
import co.ke.bigfootke.app.jpa.implementations.GroupJpaImplementation;
import co.ke.bigfootke.app.jpa.implementations.SmsJpaImplementation;
import co.ke.bigfootke.app.jpa.implementations.UserJpaImplementation;
import co.ke.bigfootke.app.pojos.SmsContainer;

@Service
public class SmsJpaService {

	@Autowired
	private SmsJpaImplementation repository;
	@Autowired
	private GroupJpaImplementation groupImpl;
	@Autowired
	private UserJpaImplementation userImpl;
	@Autowired
	private ClientJpaImplementation clientImpl;
	
	private SmsDispatcher dispatcher;

	private static final Logger log = LoggerFactory.getLogger(SmsJpaService.class);
	private Map<String, String> response;
	
	public Sms saveSms(Sms sms, List<Long> groupIds,  Long userId) {	
		Sms newSms = repository.create(sms);
		//Link groups to sms
		groupImpl.addToOnDemandSms(newSms.getSmsId(), groupIds);
		//Link sms to user
		userImpl.addToSms(newSms.getSmsId(), userId);
		return newSms;
	}

	public ResponseEntity<Object> findById(Long smsId) {
		if(!repository.exists(smsId)) {
			response = new HashMap<>();
			response.put("message", "Error: Sms not found");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Object>(repository.findById(smsId), HttpStatus.OK);
	}
	
	@Cacheable(value = "sms.paged", key="#pageNo", unless = "#result != null")
	public ResponseEntity<Page<Sms>> findAll(int pageNo, int pageSize) {
		return repository.findAll(pageNo, pageSize);
	} 
	
	@Cacheable(value = "sms.byGroup", key="#groupId", unless = "#result != null")
	public ResponseEntity<Object> findByGroup(Long groupId, int pageNo, int pageSize) {
		if(!groupImpl.exists(groupId)) {
			response.put("message", "Error: Could not find group");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);				
		}
		final Page<Sms> sms = repository.findByGroup(groupId, pageNo, pageSize);
		return new ResponseEntity<Object>(sms, HttpStatus.OK);
	} 
	
	@Caching(evict = {@CacheEvict(value = "sms.paged", key = "#smsId"),
			@CacheEvict(value = "sms.byGroup", key = "#smsId")})
	public ResponseEntity<Object> delete(Long smsId) {
		if(!repository.exists(smsId)) {
			response = new HashMap<>();
			response.put("message", "Error: Sms not found");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);			
		}
		response.put("message", "Deleted Sms record");
		repository.delete(smsId);
		return new ResponseEntity<Object>(response, HttpStatus.OK);	
	}
	
	public ResponseEntity<Object> sendSms(SmsContainer smsRequest) {
		//instantiate the sms dispatcher
		dispatcher = new SmsDispatcher();
		if(smsRequest.getMessage() != null) {
	        log.info("***** Sending sms");
	        //Create sms
			//set calendar date
			Calendar today = Calendar.getInstance();
	        Sms newSms = new Sms(smsRequest.getMessage(), smsRequest.getCost(), today.getTime());
	        //save the sent sms
	        Sms processedSms = saveSms(newSms, smsRequest.getGroupIds(), smsRequest.getSenderId());
	        //process the groups to get their clients full phoneNo
	        String recipients = processGroups(processedSms.getGroups());
			//Send to africastalking api and receive response
			return new ResponseEntity<Object>(dispatcher.sendOnDemandSms(recipients, processedSms.getMessage())
					, HttpStatus.OK);
		}
		return new ResponseEntity<Object>("ERROR: Message must be included", HttpStatus.BAD_REQUEST);
	}

	public String processGroups(Set<Group> recipientGroups) {
		String receipients = null;
		List<Long> allGroupIds = new ArrayList<>();;
		//Retrieve clients of groups
		for(Group group : recipientGroups) {
			Long groupId = group.getGroupId();
			allGroupIds.add(groupId);
		}
		receipients = clientImpl.processPhoneNos(allGroupIds);
		return receipients;
	}
}
