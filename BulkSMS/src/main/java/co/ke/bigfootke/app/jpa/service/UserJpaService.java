package co.ke.bigfootke.app.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import co.ke.bigfootke.app.jpa.entities.User;
import co.ke.bigfootke.app.jpa.implementations.CredentialsJpaImplementation;
import co.ke.bigfootke.app.jpa.implementations.UserJpaImplementation;

@Service
public class UserJpaService{
	@Autowired
	UserJpaImplementation repository;
	@Autowired
	CredentialsJpaImplementation credRepo;

	
	public ResponseEntity<Object> create(User user) {
		if(repository.findByEmail(user.getEmail()) != null)
			return new ResponseEntity<Object>(HttpStatus.CONFLICT);
		return new ResponseEntity<Object>(repository.create(user), HttpStatus.OK);		
	}
	
	public ResponseEntity<Object> findAll() {
		return new ResponseEntity<Object>(repository.findAll(), HttpStatus.OK);
	}
	
	public ResponseEntity<Object> findById(Long userId) {
		if(repository.exists(userId))
			return new ResponseEntity<Object>(repository.findById(userId), HttpStatus.OK);
		return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
	}
	
	public ResponseEntity<Object> findByEmail(String email) {
		User user = repository.findByEmail(email);
		if(user != null){
			return new ResponseEntity<Object>( user, HttpStatus.OK);
		}
		return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
	}
	
	public ResponseEntity<Object> update(User user) {
		if(repository.exists(user.getUserId()))
			return new ResponseEntity<Object>(repository.update(user), HttpStatus.OK);
		return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
	}
	
	public ResponseEntity<Object> delete(Long userId) {
		if(repository.exists(userId)) {
			repository.delete(userId);
			return new ResponseEntity<Object>( HttpStatus.OK);
		}
		return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);		
	}	
}
