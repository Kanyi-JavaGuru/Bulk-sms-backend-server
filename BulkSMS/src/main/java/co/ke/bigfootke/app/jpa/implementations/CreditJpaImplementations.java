package co.ke.bigfootke.app.jpa.implementations;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import co.ke.bigfootke.app.jpa.entities.Credit;
import co.ke.bigfootke.app.jpa.repository.CreditJpaRepo;
/**
 * A child class of MySessionManager 
 * that carries out the CRUD operations 
 * of the ClientGroup entity class 
 * **/
@Repository
public class CreditJpaImplementations{
	@Autowired
	private CreditJpaRepo repository;
	@PersistenceUnit
	EntityManagerFactory factory;
	private static final Logger log = LoggerFactory.getLogger(CreditJpaImplementations.class);
	
	public Credit findById(final Long creditId) {
		Credit credit = repository.findOne(creditId);	
		log.info("***** Found: "+credit);
		return credit;
	}
	
	public Credit update(Credit credit) {	
		final EntityManager manager = factory.createEntityManager();
		manager.getTransaction().begin();
		Credit updatedCredit = manager.merge(credit);
		manager.getTransaction().commit();
		return updatedCredit;
	}
	
}
