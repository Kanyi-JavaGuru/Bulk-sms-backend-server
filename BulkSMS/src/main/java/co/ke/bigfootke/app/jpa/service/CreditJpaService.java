package co.ke.bigfootke.app.jpa.service;

import java.text.DecimalFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;

import co.ke.bigfootke.app.jpa.entities.Credit;
import co.ke.bigfootke.app.jpa.implementations.CreditJpaImplementations;

@Service
public class CreditJpaService {
	@Autowired
	private CreditJpaImplementations repository;
	private final static Long cId = 1L;
	
	public Credit findById(Long id) {
		return repository.findById(id);
	}
	
	public void update(JsonNode  credit_amount) {
		DecimalFormat dFormat = new DecimalFormat("#0.00");
		Double addAmount = Double.parseDouble(dFormat.format(credit_amount.path("amount").asDouble()));
				
		Credit credit = repository.findById(cId);
		Double currentAmount = Double.parseDouble(dFormat.format(credit.getAmount()));
		
		Double newAmount = Double.parseDouble(dFormat.format(currentAmount + addAmount));
		credit.setAmount(newAmount);
		repository.update(credit);
	}
}
