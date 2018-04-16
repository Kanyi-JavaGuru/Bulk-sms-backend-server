package co.ke.bigfootke.app.jpa.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import co.ke.bigfootke.app.jpa.entities.Group;
import co.ke.bigfootke.app.jpa.implementations.ClientJpaImplementation;
import co.ke.bigfootke.app.jpa.implementations.GroupJpaImplementation;

@Service
public class GroupJpaService {

	@Autowired
	private GroupJpaImplementation repository;
	@Autowired
	private ClientJpaImplementation clientRepo;
	private Map<String, String> response;
	
	public ResponseEntity<Object> create(Group group) {
		response = new HashMap<>();
		if(group.getName() != null && !(repository.findByName(group.getName()) != null)) {
			Group newGroup = repository.create(group);
			response.put("message", "Success: Created "+newGroup);
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		response.put("message", "Error: Group may already exist or Group Name not included");
		return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
	}
	
	public ResponseEntity<Object> findById(Long groupId) {
		if(repository.exists(groupId)) {
			return new ResponseEntity<Object>(repository.findById(groupId), HttpStatus.OK);
		}
		response = new HashMap<>();
		response.put("message", "Error: Group not found");
		return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
	}
	
	public ResponseEntity<Object> findByName(String groupName) {
		if(repository.findByName(groupName) != null) {
			return new ResponseEntity<Object>(repository.findByName(groupName), HttpStatus.OK);
		}
		response = new HashMap<>();
		response.put("message", "Error: Group not found");
		return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
	}
	
	public ResponseEntity<Object> findAll() {
		return new ResponseEntity<Object>(repository.findAll(), HttpStatus.OK);
	}	
	
	public ResponseEntity<Object> update(Group group) {
		response = new HashMap<>();
		if(repository.exists(group.getGroupId())) {
			return new ResponseEntity<Object>(repository.update(group), HttpStatus.OK);
		}			
		response.put("message", "Error: Could not find Group");
		return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
	}
	
	public ResponseEntity<Object> delete(Long groupId) {
		response = new HashMap<>();
		if(repository.exists(groupId)) {
			response.put("message", "Deleted");
			repository.delete(groupId);
			return new ResponseEntity<Object>(response, HttpStatus.OK);				
		}
		response.put("message", "Error: Client not found");
		return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
	}

	public ResponseEntity<Object> addToOnDemandSms(Long smsId, List<Long> groupIds) {
		repository.addToOnDemandSms(smsId, groupIds);
		response = new HashMap<>();
		response.put("message", "Error: Groups have been added");
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}	

	public ResponseEntity<Object> addToSchedule(Long scheduleId, List<Long> groupIds) {
		repository.addToSchedule(scheduleId, groupIds);
		response = new HashMap<>();
		response.put("message", "Error: Groups have been added");
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	public ResponseEntity<Object> deleteFromSchedule(Long smsId, Long groupId) {
		return new ResponseEntity<Object>(repository.deleteFromSchedule(smsId, groupId), HttpStatus.OK);
	}

	public ResponseEntity<Object> calculateGroupCosts(List<Long> groupIds) {		
		return new ResponseEntity<Object>(repository.calculateCosts(groupIds), HttpStatus.OK);
	}
	
	public ResponseEntity<Object> calculateYearCosts() {		
		return new ResponseEntity<Object>(repository.calculateYearCosts(), HttpStatus.OK);
	}
	
	public String processPhoneNos(List<Long> groupIds) {
		List<Long> processedGroups = new ArrayList<>();
		for(Long groupId : groupIds) {
			if(repository.exists(groupId) && groupId > 0) {
				processedGroups.add(groupId);
			}
		}
		return clientRepo.processPhoneNos(processedGroups);
	}

	
}
