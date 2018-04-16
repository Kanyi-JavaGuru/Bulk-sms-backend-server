package co.ke.bigfootke.app.jpa.implementations;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import co.ke.bigfootke.app.jpa.entities.Sms;
import co.ke.bigfootke.app.jpa.repository.SmsJpaRepo;

@Repository
public class SmsJpaImplementation {

	@Autowired
	private SmsJpaRepo repository;
	@PersistenceUnit
	EntityManagerFactory factory;
	private static final Logger log = LoggerFactory.getLogger(SmsJpaImplementation.class);
	
	public boolean exists(Long smsId) {
		return repository.exists(smsId);
	}
	
	public Sms create(Sms sms) {
		Sms newSms = repository.save(sms);	
		log.info("***** Created: "+newSms);
		return newSms;
	}
	
	public Sms findById(final Long smsId) {
		Sms sms = repository.findOne(smsId);	
		log.info("***** Found: "+sms);
		return sms;
	}
	
	public ResponseEntity<Page<Sms>> findAll(final int pageNo, final int pageSize){
		Page<Sms> smss = repository.findAll(new PageRequest(pageNo, pageSize));
		//send the response to the webClient
		return new ResponseEntity<Page<Sms>>(smss, HttpStatus.OK);
	}
	
	public void delete(final Long smsId){		
		final Sms sms = findById(smsId);
		log.info("***** Removing groups assigned to sms");
		sms.getGroups().removeAll(sms.getGroups());		
		log.info("***** Removing sender of sms");
		repository.delete(smsId);		
		log.info("***** deleted: "+sms);
	}
	
	public Page<Sms> findByGroup(final Long groupId, final int pageNo, final int pageSize){
		return repository.findByGroupsGroupIdOrderByDate(groupId, new PageRequest(pageNo, pageSize));		
	}
	
}
