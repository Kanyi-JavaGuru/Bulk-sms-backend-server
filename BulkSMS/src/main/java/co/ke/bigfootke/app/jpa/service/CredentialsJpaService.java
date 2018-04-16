package co.ke.bigfootke.app.jpa.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import co.ke.bigfootke.app.jpa.entities.Credentials;
import co.ke.bigfootke.app.jpa.entities.User;
import co.ke.bigfootke.app.jpa.implementations.CredentialsJpaImplementation;
import co.ke.bigfootke.app.jpa.implementations.UserJpaImplementation;

@Service
public class CredentialsJpaService{
	@Autowired
	CredentialsJpaImplementation repository;
	@Autowired
	UserJpaImplementation userRepo;
	private Map<String, String> response;
	
	public ResponseEntity<Object> create(Credentials credentials, String email) {
		response = new HashMap<>();
		//check email exists
		if(userRepo.findByEmail(email) != null) {
			//check if they have credentials
			User user = userRepo.findByEmail(email);
			if (findByUserId(user.getUserId()) != null) {
				//inform them to inform their admin to reset their account if they have forgotten password
				response.put("message", "Error: Inform Admin to reset password if forgotten! ");
				return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
			}else {
				//save their credentials and to tell them to login
				repository.create(user ,credentials);	
				response.put("message", "Success: Login with your new credentials!");
				return new ResponseEntity<>(response, HttpStatus.OK);			
			}
		}
		return null;
	}
	
	private Credentials findByUserId(Long userId) {
		return repository.findByUserId(userId);
	}

	public ResponseEntity<Object> changePassword(Long userId, String oldPassword, String newPassword) {
		response = new HashMap<>();
		//check if user exists
		if(userRepo.exists(userId)) {
			//retrieve user's credentials
			Credentials cred = findByUserId(userId);
			//check if oldPasswords match
			if(cred.getPassword() != oldPassword) {
				//cannot change password
				response.put("message", "Error: Passwords do not match! ");
				return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
			}else {
				//change password
				cred.setPassword(newPassword);
				repository.update(cred);
				response.put("message", "Success: Password changed! ");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		}
		return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
	}		
}
