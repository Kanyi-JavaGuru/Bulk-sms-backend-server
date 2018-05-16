package co.ke.bigfootke.app.jpa.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import co.ke.bigfootke.app.jpa.entities.Credentials;
import co.ke.bigfootke.app.jpa.entities.User;
import co.ke.bigfootke.app.jpa.implementations.CredentialsJpaImplementation;
import co.ke.bigfootke.app.jpa.implementations.UserJpaImplementation;
import co.ke.bigfootke.app.user.authentication.AuthenticationFacade;

@Service
public class CredentialsJpaService{
	@Autowired
	CredentialsJpaImplementation repository;
	@Autowired
	UserJpaImplementation userRepo;
	@Autowired
	private AuthenticationFacade authentication;
	
	private Map<String, String> response;	
	private static final Logger log = LoggerFactory.getLogger(CredentialsJpaService.class);

	public ResponseEntity<Object> create(Credentials credentials, Long userId) {
		response = new HashMap<>();
		if (findByUserId(userId) != null) {
			//inform them to inform their admin to reset their account if they have forgotten password
			response.put("message", "Error: Inform Admin to reset password if forgotten! ");
			return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
		}
		//save their credentials and to tell them to login
		repository.create(userRepo.findById(userId) ,credentials);	
		response.put("message", "Success: Login with your new credentials!");
		return new ResponseEntity<>(response, HttpStatus.OK);			
	}
	
	private Credentials findByUserId(Long userId) {
		return repository.findByUserId(userId);
	}

	public ResponseEntity<Object> changePassword(String oldPassword, String newPassword) {
		response = new HashMap<>();
		//retrieve currently signed in user
		User currentUser = (User) authentication.getAuthentication().getPrincipal();
		//retrieve user's credentials
		Credentials cred = findByUserId(currentUser.getUserId());
		//check if oldPasswords match
		if(!cred.getPassword().trim().equals(oldPassword.trim())) {
			//cannot change password
			response.put("message", "Error: Passwords do not match! "+cred.getPassword().trim()+" "+oldPassword.trim());
			return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
		}
		//change password
		cred.setPassword(newPassword);
		repository.update(cred);
		response.put("message", "Successfully changed password! ");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<Object> signIn() {	
		User signedInUser = (User) authentication.getAuthentication().getPrincipal();	
		log.info("***** Signed In: "+signedInUser);
		
		//retrieve user's credentials
		Credentials cred = findByUserId(signedInUser.getUserId());
		
		//Retrieve current date
		Calendar date = Calendar.getInstance(TimeZone.getTimeZone("Africa/Kenya"));
		Date siginDate = date.getTime();
		
		//update signed in user credentials
		cred.setLast_sign_in(siginDate);
		cred.setLoggedIn(true);
		repository.update(cred);
		
		return new ResponseEntity<>(signedInUser, HttpStatus.OK);
	}
	
	public ResponseEntity<Object> signOut() {
		User signingOutUser = (User) authentication.getAuthentication().getPrincipal();
		response = new HashMap<>();
		
		//retrieve user's credentials
		Credentials cred = findByUserId(signingOutUser.getUserId());
		cred.setLoggedIn(false);
		repository.update(cred);
		
		response.put("message", "Successfully signed Out ");		
		log.info("***** Signed Out: "+signingOutUser);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<Object> getRole() {
		User signedInUser = (User) authentication.getAuthentication().getPrincipal();
		String userRole = signedInUser.getCredentials().getRole();
		Map<String, String> roleResponse = new HashMap<>();
		roleResponse.put("role", userRole);
		return new ResponseEntity<>(roleResponse, HttpStatus.OK);
	}
		
}
