package co.ke.bigfootke.app.jpa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.ke.bigfootke.app.jpa.entities.Group;
import co.ke.bigfootke.app.jpa.service.GroupJpaService;
import co.ke.bigfootke.app.pojos.Family;

@RestController
@RequestMapping(value = "api/group")
@CrossOrigin(origins="http://localhost:4200")
public class GroupJpaController {
	@Autowired
	private GroupJpaService service;
	
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Object> create(@RequestBody Group group) {		
		return service.create(group);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Object> findAll() {
		return service.findAll();	
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/{groupId}")
	public ResponseEntity<Object> findById(@PathVariable Long groupId) {
		return service.findById(groupId);	
	}
	
	@RequestMapping(method=RequestMethod.GET, value = "/name/{name}")
	public ResponseEntity<Object> findByName(@PathVariable String name) {
		return service.findByName(name);
	}
		
	@RequestMapping(method=RequestMethod.PUT)
	public ResponseEntity<Object> update(@RequestBody Group group) {
		return service.update(group);
	}
	
	@RequestMapping(method=RequestMethod.DELETE, value = "/delete/{groupId}")	
	public ResponseEntity<Object> delete(@PathVariable Long groupId){
		return service.delete(groupId);
	}
		
	@RequestMapping(method=RequestMethod.POST, value="/add")
	public ResponseEntity<Object> addToOnDemandSms(@RequestBody Family family) {		
		return service.addToOnDemandSms(family.getParentId(), family.getChildrenIds());
	}
	
	@RequestMapping(method=RequestMethod.POST, value="/group")
	public ResponseEntity<Object> addToSchedule(@RequestBody Family family) {		
		return service.addToSchedule(family.getParentId(), family.getChildrenIds());
	}
	
	@RequestMapping(method=RequestMethod.DELETE, value = "/delete/{smsId}/from/{groupId}")	
	public ResponseEntity<Object> deleteFromSchedule(@PathVariable Long smsId, @PathVariable Long groupId){
		return service.deleteFromSchedule(smsId, groupId);
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/costs")
	public ResponseEntity<Object> calculateGroupCosts(@RequestBody Family family) {		
		return service.calculateGroupCosts(family.getChildrenIds());
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/costs/year")
	public ResponseEntity<Object> calculateYearCosts() {		
		return service.calculateYearCosts();
	}
}
