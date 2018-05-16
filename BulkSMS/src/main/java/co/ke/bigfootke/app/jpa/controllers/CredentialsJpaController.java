package co.ke.bigfootke.app.jpa.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.ke.bigfootke.app.jpa.entities.Credentials;
import co.ke.bigfootke.app.jpa.repository.UserJpaRepo;
import co.ke.bigfootke.app.jpa.service.CredentialsJpaService;

@RestController
@RequestMapping(value = "api/credentials")
public class CredentialsJpaController {

	@Autowired
	private CredentialsJpaService service;
	@Autowired
	UserJpaRepo userRepository;
	
	private Map<String, String> response;
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method=RequestMethod.POST, value = "/create")
	public ResponseEntity<Object> createCredentials(
			@RequestParam("UserId") Long userId, @RequestParam("isActive") boolean isActive,
			@RequestParam("Password") String password, @RequestParam("Role") String role) {
		if(password != null & userId != null && password != null && role != null) {
			if(userRepository.exists(userId)) {
				//save and link credentials to user id
				Credentials credentials = new Credentials();
				credentials.setActive(isActive);
				credentials.setPassword(password);
				credentials.setRole(role);
				return service.create(credentials, userId);		
			}
		}
		response = new HashMap<>();
		response.put("message", "Error: User does not exists! ");
		return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
	}
	
	@RequestMapping(method=RequestMethod.PUT)
	public ResponseEntity<Object> signIn() {
		return service.signIn();
	}
	
	@RequestMapping(method=RequestMethod.PUT,  value = "/signout")
	public ResponseEntity<Object> signOut() {
		return service.signOut();
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<Object> getRole() {
		return service.getRole();
	}
	/**ensures that authenticated user changes their own password
	 * @param old password
	 * @param new password**/
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Object> changePassword(@RequestParam("OldPassword") String oldPassword, 
			@RequestParam("NewPassword") String newPassword) {
		return service.changePassword(oldPassword, newPassword);
	}	
}
