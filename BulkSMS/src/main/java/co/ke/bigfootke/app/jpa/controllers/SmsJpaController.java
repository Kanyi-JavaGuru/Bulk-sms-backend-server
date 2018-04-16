package co.ke.bigfootke.app.jpa.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.ke.bigfootke.app.jpa.entities.Sms;
import co.ke.bigfootke.app.jpa.service.SmsJpaService;
import co.ke.bigfootke.app.pojos.SmsContainer;

@RestController
@RequestMapping(value = "api/sms")
@CrossOrigin(origins="http://localhost:4200")
public class SmsJpaController {

	@Autowired
	private SmsJpaService service;
	
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Object> sendSms(@RequestBody SmsContainer smsContainer) {		
		return service.sendSms(smsContainer);
	}
	@RequestMapping(method = RequestMethod.GET, value = "/page/{pageNo}/size/{pageSize}")
	public ResponseEntity<Page<Sms>> findAll(@PathVariable int pageNo, @PathVariable int pageSize) {
		return service.findAll(pageNo, pageSize);	
	}
	@RequestMapping(method = RequestMethod.GET, value = "/{smsId}")
	public ResponseEntity<Object> findById(@PathVariable Long smsId) {
		return service.findById(smsId);	
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/group/{groupId}/page/{pageNo}/size/{pageSize}")
	public ResponseEntity<Object> findByGroup(@PathVariable Long groupId, @PathVariable int pageNo, 
													@PathVariable int pageSize) {
		return service.findByGroup(groupId, pageNo, pageSize);	
	}
	
	@RequestMapping(method=RequestMethod.DELETE, value = "/delete/{smsId}")	
	public ResponseEntity<Object> delete(@PathVariable Long smsId){
		return service.delete(smsId);
	}
	
}
