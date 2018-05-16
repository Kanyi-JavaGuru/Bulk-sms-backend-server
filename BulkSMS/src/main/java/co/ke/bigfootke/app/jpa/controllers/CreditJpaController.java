package co.ke.bigfootke.app.jpa.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import co.ke.bigfootke.app.jpa.entities.Credit;
import co.ke.bigfootke.app.jpa.service.CreditJpaService;

@RestController
@RequestMapping(value = "api/credit")
public class CreditJpaController {
	
	@Autowired
	CreditJpaService service;
	private Map<String, String> response;
	
	/**GET Credit amount **/
	@RequestMapping(method=RequestMethod.GET)
	public Credit getCredit() {
		return service.findById(1L);
	}
	
	/**UPDATE Credit**/
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method=RequestMethod.PUT)
	public ResponseEntity<Object> topUp(@RequestBody @Valid JsonNode credit_amount, BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			response = new HashMap<>();
			for(FieldError error:bindingResult.getFieldErrors()) {
				response.put(error.getField(), error.getDefaultMessage());
			}
			return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);//response status 406
		}
		if(credit_amount.path("amount").asInt() == 0 || credit_amount.path("amount").asInt() < 0) {
			response = new HashMap<>();
			response.put("Amount", "Invalid Amount. Can't be zero or negative");
			return new ResponseEntity<>( response, HttpStatus.FORBIDDEN);//response status 403
		}
		service.update(credit_amount);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
