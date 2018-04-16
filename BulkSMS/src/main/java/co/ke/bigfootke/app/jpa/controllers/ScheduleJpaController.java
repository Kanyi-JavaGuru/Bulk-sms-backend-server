package co.ke.bigfootke.app.jpa.controllers;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.ke.bigfootke.app.jpa.entities.ScheduledSms;
import co.ke.bigfootke.app.jpa.service.ScheduleJpaService;
import co.ke.bigfootke.app.pojos.ScheduledSmsContainer;

@RestController
@RequestMapping(value = "api/schedule")
@CrossOrigin(origins="http://localhost:4200")
public class ScheduleJpaController {

	@Autowired
	private ScheduleJpaService service;
	
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Object> create(@RequestBody ScheduledSmsContainer container) {		
		return service.create(container);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/page/{pageNo}/size/{pageSize}")
	public ResponseEntity<Page<ScheduledSms>> findAll(@PathVariable int pageNo, @PathVariable int pageSize) {
		return service.findAll(pageNo, pageSize);	
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/{scheduleId}")
	public ResponseEntity<Object> findById(@PathVariable Long scheduleId) {
		return service.findById(scheduleId);	
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/title/{title}")
	public ResponseEntity<Object> findByTitle(@PathVariable String title) {
		return service.findByTitle(title);	
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/date")
	public ResponseEntity<Object> findByDate(@RequestParam("date") 
											@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") Date date) {
		return service.findByDate(date);	
	}
	@RequestMapping(method = RequestMethod.GET, value = "/dates")
	public ResponseEntity<Object> findBtwnDates(@RequestParam("firstDate") 
											@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") Date firstDate,
											@RequestParam("lastDate") 
											@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") Date lastDate) {
		return service.findBtwnDates(firstDate, lastDate);	
	}
	@RequestMapping(method = RequestMethod.GET, value = "/group/{groupId}/page/{pageNo}/size/{pageSize}")
	public ResponseEntity<Object> findByGroup(@PathVariable Long groupId, @PathVariable int pageNo, 
													@PathVariable int pageSize) {
		return service.findByGroup(groupId, pageNo, pageSize);	
	}
	
	@RequestMapping(method=RequestMethod.DELETE, value = "/delete/{scheduleId}")	
	public ResponseEntity<Object> delete(@PathVariable Long scheduleId){
		return service.delete(scheduleId);
	}
	
}
