package co.ke.bigfootke.app.jpa.implementations;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import co.ke.bigfootke.app.jpa.entities.Group;
import co.ke.bigfootke.app.jpa.entities.Sms;
import co.ke.bigfootke.app.jpa.entities.ScheduledSms;
import co.ke.bigfootke.app.jpa.repository.ClientJpaRepo;
import co.ke.bigfootke.app.jpa.repository.GroupJpaRepo;

@Repository
public class GroupJpaImplementation {

	@Autowired
	private GroupJpaRepo repository;
	@Autowired
	private ClientJpaRepo clientRepo;
	@Autowired
	private SmsJpaImplementation smsImpl;
	@Autowired
	private ScheduleJpaImplementation scheduleImpl;
	@PersistenceUnit
	EntityManagerFactory factory;
	private static final Logger log = LoggerFactory.getLogger(GroupJpaImplementation.class);

	private Map<String, String> response;
	
	public boolean exists(Long groupId) {
		return repository.exists(groupId);
	}
	
	public Group create(Group group) {
		Group newGroup = repository.save(group);	
		log.info("***** Created: "+newGroup);
		return newGroup;
	}

	public List<Group> findAll() {
		return repository.findAll();
	}
	
	public Group findById(final Long groupId) {
		Group group = repository.findOne(groupId);	
		log.info("***** Found: "+group);
		return group;
	}

	public Group findByName(String groupName) {
		Group group = repository.findByName(groupName);	
		log.info("***** Found: "+group);
		return group;
	}
		
	public Group update(Group newGroup){
		Group oldGroup = repository.findOne(newGroup.getGroupId());
		Group updated = null;
		final EntityManager manager = factory.createEntityManager();
		manager.getTransaction().begin();
		log.info("***** Updated: "+newGroup);
		if(repository.findByName(newGroup.getName()) != null) {
			if(repository.findByName(newGroup.getName()).getGroupId() != newGroup.getGroupId()) {
				log.info("***** Update failed: Group name already exists");
				return null;
			}else {
				updated = manager.merge(newGroup);
				log.info("***** Updated: from "+oldGroup+" to "+updated);
			}
		}else {
			updated = manager.merge(newGroup);
			log.info("***** Updated: from "+oldGroup+" to "+updated);
		}
		manager.getTransaction().commit();
		return updated;
	}
	
	public void delete(Long groupId){
		final Group group = findById(groupId);
		log.info("***** Removing clients assigned to group");
		group.getClients().removeAll(group.getClients());
		log.info("***** Removing schedules assigned to group");
//		group.getSchedules().removeAll(group.getSchedules());
		repository.delete(groupId);
	}

	public void addToOnDemandSms(Long smsId, List<Long> groupIds){
		final EntityManager manager = factory.createEntityManager();
		Sms sms = smsImpl.findById(smsId);
		manager.getTransaction().begin();
		Set<Group> processedGroups = new HashSet<>();
		if(sms.getGroups() != null) {
			for(Long groupId: groupIds) {
				if(exists(groupId)) {
					Group group = findById(groupId);
					sms.getGroups().add(group);
					log.info("***** Added "+group+" to "+sms);
				}
			}
		}else {
			for(Long groupId: groupIds) {
				if(exists(groupId)) {
					Group group = findById(groupId);
					processedGroups.add(group);
					log.info("***** Added group "+group+" to "+sms);
				}
			}
			sms.setGroups(processedGroups);
		}
		manager.merge(sms);
		manager.getTransaction().commit();
	}
	
	public void addToSchedule(Long scheduleId, List<Long> groupIds){
		final EntityManager manager = factory.createEntityManager();
		ScheduledSms schedule = scheduleImpl.findById(scheduleId);
		manager.getTransaction().begin();
		Set<Group> processedGroups = new HashSet<>();
		if(schedule.getGroups() != null) {
			for(Long groupId: groupIds) {
				if(exists(groupId)) {
					Group group = findById(groupId);
					schedule.getGroups().add(group);
					log.info("***** Added group "+group+" to "+schedule);
				}
			}
		}else {
			for(Long groupId: groupIds) {
				if(exists(groupId)) {
					Group group = findById(groupId);
					processedGroups.add(group);
					log.info("***** Added group "+group+" to "+schedule);
				}
			}
			schedule.setGroups(processedGroups);
		}		
		manager.merge(schedule);
		manager.getTransaction().commit();
	}
	
	public ResponseEntity<Object> deleteFromSchedule(Long scheduleId, Long groupId){
		final EntityManager manager = factory.createEntityManager();
		ScheduledSms schedule = scheduleImpl.findById(scheduleId);
		Group group = null;
		manager.getTransaction().begin();
		if(exists(groupId)) {
			group = findById(groupId);
			schedule.getGroups().remove(group);
			log.info("***** Added group "+group+" to "+schedule);
		}
		manager.merge(schedule);
		manager.getTransaction().commit();
		response.put("message", "Success: Deleted: "+group+" from "+schedule);		
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	/**Calculates the total cost of all groups 
	 * added to a schedule or OnDemandSms
	 * @param List of groupids**/
	public int calculateCosts(List<Long> groupIds){
		int cost = 0;
		for(Long groupId : groupIds) {
			int costPerGroup = clientRepo.findByGroupsGroupIdOrderByPhoneNo(groupId).size();
			cost = cost +costPerGroup;
		}
		return cost;
	}
	
}
