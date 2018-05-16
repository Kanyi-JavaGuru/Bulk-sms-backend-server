package co.ke.bigfootke.app.jpa.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.ke.bigfootke.app.jpa.entities.User;
import co.ke.bigfootke.app.jpa.implementations.UserJpaImplementation;
import co.ke.bigfootke.app.jpa.service.UserJpaService;

@RestController
@RequestMapping(value = "api/user")
public class UserJpaController {

	@Autowired
	private UserJpaService service;
	@Autowired
	UserJpaImplementation repository;
	
	private Map<String, String> response;
	
	/**CREATE USER**/
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Object> create( @RequestParam("firstName") String firstName, 
										@RequestParam("lastName") String lastName, @RequestParam("email") String email) {
		response = new HashMap<>();
		if(email != null) {
			User userExists = repository.findByEmail(email);
			if(userExists!=null) {
				response.put("Error", "User already exists");
				return new ResponseEntity<>( response, HttpStatus.CONFLICT);
			}
			//create new user object
			User user = new User();
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setEmail(email);
			//save new user
			return service.create(user);
		}
		response.put("Error", "email is required!");
		return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
	}
	
	/**GET ALL USERS**/
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<Object> findAll() {
		return service.findAll();
	}
	/**GET USER BY ID**/
	@RequestMapping(method=RequestMethod.GET, value = "/{id}")
	public ResponseEntity<Object> get(@PathVariable Long id) {
		return service.findById(id);
	}
	/**GET USER BY USERNAME**/
	@RequestMapping(method=RequestMethod.GET, value = "/email/{email}/")
	public ResponseEntity<Object> get(@PathVariable String email) {
		return service.findByEmail(email);
	}
	/**UPDATE USER**/
	@RequestMapping(method=RequestMethod.PUT)
	public void update(@RequestBody User user) {
		service.update(user);
	}
	/**DELETE USER**/
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method=RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<Object> delete(@PathVariable Long id) {
		return service.delete(id);
	}	
}
