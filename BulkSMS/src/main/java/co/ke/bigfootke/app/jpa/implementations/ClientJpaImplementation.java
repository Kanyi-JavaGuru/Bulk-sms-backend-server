package co.ke.bigfootke.app.jpa.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import co.ke.bigfootke.app.jpa.entities.Client;
import co.ke.bigfootke.app.jpa.entities.Group;
import co.ke.bigfootke.app.jpa.repository.ClientJpaRepo;

@Repository
public class ClientJpaImplementation {

	@Autowired
	private ClientJpaRepo repository;
	@Autowired
	private GroupJpaImplementation groupImpl;
	@PersistenceUnit
	EntityManagerFactory factory;
	private static final Logger log = LoggerFactory.getLogger(ClientJpaImplementation.class);

	private Map<String, String> response;
	private List<Object> responseList;
	
	public boolean exists(Long clientId) {
		return repository.exists(clientId);
	}
	
	public Client create(Client client) {
		Client newClient = repository.save(client);	
		log.info("***** Created: "+newClient);
		return newClient;
	}
	
	public Client findById(final Long clientId) {
		Client client = repository.findOne(clientId);	
		log.info("***** Found: "+client);
		return client;
	}

	public Client findByPhone(String phoneNo) {
		return repository.findByPhoneNo(phoneNo);
	}
	
	public ResponseEntity<Page<Client>> findAll(final int pageNo, final int pageSize){
		Page<Client> clients = repository.findAll(new PageRequest(pageNo, pageSize));
		//send the response to the webClient
		return new ResponseEntity<Page<Client>>(clients, HttpStatus.OK);
	}
	
	/**Batch inserts new Clients to the database**/
	public void batchCreateNewClients(List<Client> newClients) {
		for(int i=0; i<newClients.size(); i++) {
			Client client = newClients.get(i);
			log.info("***** Saving: "+client+" completed.");
			repository.save(client);
		}
	}
	
	/**Batch Processes all clients received
	 * by checking if they exist in the database 
	 * then saving to database if they don't**/
	public List<Client> batchCheckIfClientExist(List<Client> rawClients) {
		List<Client> newClients = new ArrayList<>();
			for(int i=0; i<rawClients.size(); i++) {
				Client client = rawClients.get(i);
				if(repository.findByPhoneNo(client.getPhoneNo()) == null)
					newClients.add(client);
			}
		return newClients;
	}
	
	public Client update(final Client newClient){
		Client oldClient = repository.findOne(newClient.getClientId());
		Client updatedClient = null;
		final EntityManager manager = factory.createEntityManager();
		manager.getTransaction().begin();
		if(repository.findByPhoneNo(newClient.getPhoneNo()) != null) {
			if(repository.findByPhoneNo(newClient.getPhoneNo()).getClientId() != newClient.getClientId()) {
				log.info("***** Update failed: Phone No already exists");
				return null;
			}else {
				updatedClient = manager.merge(newClient);
				log.info("***** Updated: from "+oldClient+" to "+updatedClient);
			}
		}else {
			updatedClient = manager.merge(newClient);
			log.info("***** Updated: from "+oldClient+" to "+updatedClient);
		}
		manager.getTransaction().commit();
		return updatedClient;
	}
	
	public void delete(final Long clientId){		
		final Client client = findById(clientId);
		log.info("***** Removing groups assigned to client");
		client.getGroups().removeAll(client.getGroups());		
		repository.delete(clientId);		
		log.info("***** deleted: "+client);
	}

	public ResponseEntity<Object> addToGroup(final Long groupId, final List<Long> clientIds) {
		final EntityManager manager = factory.createEntityManager();
		manager.getTransaction().begin();
		
		final Group group = groupImpl.findById(groupId);
		log.info("***** Retrieved: "+group);
		
		responseList = new ArrayList<>();
		log.info("***** Adding clients to Group");
		
		for(Long clientId: clientIds) {
			response = new HashMap<>();
			final Client client = repository.findOne(clientId);
			group.getClients().add(client);
			response.put("message", "Successfuly added "+client+" to "+group);
			responseList.add(response);	
			log.info("***** Added Client: "+client+" to group"+group);
		}	
		manager.merge(group);
		manager.getTransaction().commit();
		return new ResponseEntity<Object>(responseList, HttpStatus.OK);
	}
	
	public Page<Client> findByGroup(final Long groupId, final int pageNo, final int pageSize){
		return repository.findByGroupsGroupIdOrderByPhoneNo(groupId, new PageRequest(pageNo, pageSize));		
	}
	
	/**Retrieves a list of all clients from a list of groups
	 * retrieves the client's country code and phone No
	 * combines country code and phone No to one
	 * returns string of combined phoneNo 
	 * to be processed by SmsDispatcher***/
	public String processPhoneNos(List<Long> groupIds) {
		StringBuffer buffer = new StringBuffer();
		for(Long groupId : groupIds) {
			List<Client> clients = repository.findByGroupsGroupIdOrderByPhoneNo(groupId);
			for(Client client : clients) {
				String countryCode = client.getCountryCode();
				String phoneNo = client.getPhoneNo();
				StringBuffer fullPhoneNo = new StringBuffer();
				fullPhoneNo.append(countryCode);
				fullPhoneNo.append(phoneNo);
				buffer.append(fullPhoneNo.toString());
				buffer.append(",");
			}
		}
		String phoneNos = buffer.toString();
		log.info("***** Added phones No: "+phoneNos);
		return phoneNos;
	}
	
	public void deleteFromGroup(final Long clientId, final Long groupId) {
		final EntityManager manager = factory.createEntityManager();
		manager.getTransaction().begin();
		
		final Group group = groupImpl.findById(groupId);
		final Client client = findById(clientId);
		group.getClients().remove(client);
		log.info("***** Removed client "+client+" from "+group);
		manager.merge(group);
		
		manager.getTransaction().commit();		
	}
}
