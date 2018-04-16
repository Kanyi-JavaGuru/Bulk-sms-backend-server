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
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.ke.bigfootke.app.jpa.entities.Credentials;
import co.ke.bigfootke.app.jpa.entities.User;
import co.ke.bigfootke.app.jpa.service.CredentialsJpaService;
import co.ke.bigfootke.app.jpa.service.UserJpaService;
import co.ke.bigfootke.app.pojos.PasswordContainer;

@RestController
@RequestMapping(value = "api/credentials")
@CrossOrigin(origins="http://localhost:4200")
public class CredentialsJpaController {

	@Autowired
	private CredentialsJpaService service;

	@Autowired
	private UserJpaService userService;
	
	private Map<String, String> errors;
	
	/**CREATE USER CREDENTIALS**/
	@RequestMapping(method=RequestMethod.POST,  value = "/{email}")
	public ResponseEntity<Object> createCredentials(@RequestBody @Valid Credentials credentials, 
			BindingResult bindingResult, @PathVariable String email) {
		if(bindingResult.hasErrors()) {
			errors = new HashMap<>();
			for(FieldError error:bindingResult.getFieldErrors()) {
				errors.put(error.getField(), error.getDefaultMessage());
			}
			return new ResponseEntity<>(errors, HttpStatus.NOT_ACCEPTABLE);
		}
		User userExists = userService.findByEmail(credentials.getUser().getEmail());
		if(userExists!=null) {
			errors = new HashMap<>();
			errors.put("Transaction Error", "User already exists");
			return new ResponseEntity<>( errors, HttpStatus.CONFLICT);
		}
		service.create(credentials, email);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Object> changePassword(@RequestBody PasswordContainer container) {
		return service.changePassword(container.getUserId(), container.getOldPassword()
				, container.getNewPassword());
	}	
}
