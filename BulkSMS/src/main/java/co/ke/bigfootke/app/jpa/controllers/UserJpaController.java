package co.ke.bigfootke.app.jpa.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.ke.bigfootke.app.jpa.entities.User;
import co.ke.bigfootke.app.jpa.service.UserJpaService;

@RestController
@RequestMapping(value = "api/user")
@CrossOrigin(origins="http://localhost:4200")
public class UserJpaController {

	@Autowired
	private UserJpaService service;
	
	private Map<String, String> errors;
	
	/**CREATE USER**/
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Object> saveUser(@RequestBody @Valid User user, BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			errors = new HashMap<>();
			for(FieldError error:bindingResult.getFieldErrors()) {
				errors.put(error.getField(), error.getDefaultMessage());
			}
			return new ResponseEntity<>(errors, HttpStatus.NOT_ACCEPTABLE);
		}
		User userExists = service.findByEmail(user.getEmail());
		if(userExists!=null) {
			errors = new HashMap<>();
			errors.put("Transaction Error", "User name already exists");
			return new ResponseEntity<>( errors, HttpStatus.CONFLICT);
		}
		service.create(user);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**GET ALL USERS**/
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
	@RequestMapping(method=RequestMethod.GET, value = "/email/{email:.+}")
	public User get(@PathVariable String email) {
		return service.findByEmail(email);
	}
	/**UPDATE USER**/
	@RequestMapping(method=RequestMethod.PUT)
	public void update(@RequestBody User user) {
		service.update(user);
	}
	/**DELETE USER**/
	@RequestMapping(method=RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<Object> delete(@PathVariable Long id) {
		return service.delete(id);
	}	
}
