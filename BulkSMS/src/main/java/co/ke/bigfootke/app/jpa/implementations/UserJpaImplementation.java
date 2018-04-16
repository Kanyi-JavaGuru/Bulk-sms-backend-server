package co.ke.bigfootke.app.jpa.implementations;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import co.ke.bigfootke.app.jpa.entities.Sms;
import co.ke.bigfootke.app.jpa.entities.User;
import co.ke.bigfootke.app.jpa.repository.UserJpaRepo;

@Repository
public class UserJpaImplementation {
	@Autowired
	UserJpaRepo repository;
	@Autowired
	SmsJpaImplementation userImpl;
	@PersistenceUnit
	EntityManagerFactory factory;
	private static final Logger log = LoggerFactory.getLogger(UserJpaImplementation.class);
	
	public boolean exists(Long userId) {
		return repository.exists(userId);
	}
	
	public User create(User user) {
		User newUser = repository.save(user);	
		log.info("***** Created: "+newUser);
		return newUser;
	}
	
	public User findById(Long userId) {
		User user = repository.findOne(userId);	
		log.info("***** Found: "+user);
		return user;		
	}
	
	public User findByEmail(String email) {
		return repository.findByEmail(email);
	}
	
	public List<User> findAll() {
		List<User> users = repository.findAll();
		return users;	
	}
	/**
	 * users are deleted by email rather than id**/
	public void delete(Long userId) {
		if(exists(userId)) {
			final User user = findById(userId);	
			repository.delete(userId);		
			log.info("***** deleted: "+user);	
		}
	}

	public User update(User newUser) {
		User oldUser = repository.findOne(newUser.getUserId());
		User updatedUser = null;
		final EntityManager manager = factory.createEntityManager();
		manager.getTransaction().begin();
		
		if(repository.findByEmail(newUser.getEmail()).getUserId() != newUser.getUserId()) {
			log.info("***** Update failed: Cannot change email");
			return null;
		}else {				
			updatedUser = manager.merge(newUser);
			log.info("***** Updated: from "+oldUser+" to "+updatedUser);
		}
		manager.getTransaction().commit();
		return updatedUser;
	}

	public ResponseEntity<Object> addToSms(Long smsId, Long userId) {
		final EntityManager manager = factory.createEntityManager();
		manager.getTransaction().begin();
		
		final Sms sms = userImpl.findById(smsId);
		if(exists(userId)) {
			final User sender = findById(userId);
			sms.setSender(sender);
			log.info("***** Link: "+sms+" to the sender "+sender);
			manager.merge(sms);
			manager.getTransaction().commit();
		}
		return new ResponseEntity<Object>(sms, HttpStatus.OK);
	}
}
