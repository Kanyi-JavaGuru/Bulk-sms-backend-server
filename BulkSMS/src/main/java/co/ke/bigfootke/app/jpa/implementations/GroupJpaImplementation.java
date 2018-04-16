package co.ke.bigfootke.app.jpa.implementations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import co.ke.bigfootke.app.jpa.entities.Group;
import co.ke.bigfootke.app.jpa.entities.Sms;
import co.ke.bigfootke.app.jpa.entities.ScheduledSms;
import co.ke.bigfootke.app.jpa.entities.ScheduledSmsCost;
import co.ke.bigfootke.app.jpa.repository.ClientJpaRepo;
import co.ke.bigfootke.app.jpa.repository.GroupJpaRepo;
import co.ke.bigfootke.app.pojos.LineChartData;

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
	
	/****
	public ResponseEntity<Object> calculateYearCosts(){
		final EntityManager manager = factory.createEntityManager();
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		int firstMonthOfCurrentYear = 0;// 0 = january
		int lastMonthOfCurrentYear = 11;// 11 = december
		int firstDate = 1;
		LineChartData yearExpenditure = new LineChartData();
		List<LineChartData> expenditureData = new ArrayList<>();
		Calendar currentYear = Calendar.getInstance(TimeZone.getTimeZone("Africa/Kenya"));
		//iterate through the months of the year
		for(int month = firstMonthOfCurrentYear; month<=lastMonthOfCurrentYear; month++) {
			//set month starting with 0=january
			currentYear.set(Calendar.MONTH, month);
			//set DATE to 1st, so first date of that month
			currentYear.set(Calendar.DATE, firstDate);
			//get the date for the first day of that month
			Date firstDateOfMonth = currentYear.getTime();
			// set actual maximum date of that month
			currentYear.set(Calendar.DATE, currentYear.getActualMaximum(Calendar.DAY_OF_MONTH));
			//get the date for the last day of that month
			Date lastDateOfMonth = currentYear.getTime();
			
			//initial month expenditure is zero
			int monthExpenditure = 0;
			//Query to get that months sms's between first and last dates
			CriteriaQuery<OnDemandSms> query = builder.createQuery(OnDemandSms.class);
			Root<OnDemandSms> root = query.from(OnDemandSms.class);
			Path<Date> date = root.get("date");
			Predicate predicate = builder.between(date, firstDateOfMonth, lastDateOfMonth);
			query.where(predicate);
			List<OnDemandSms> perMonthSmsList =  manager.createQuery(query).getResultList();
			//if not expenses for that month calculate the total expenditure
			if(perMonthSmsList != null) {
				for(OnDemandSms sms : perMonthSmsList) {
					monthExpenditure = monthExpenditure + sms.getCost().intValue();
				}
			}
			//add that months totalExpenditure to list
			yearExpenditure.getData().add(monthExpenditure);
			
		}
		yearExpenditure.setLabel(Integer.toString(currentYear.get(Calendar.YEAR))+" - OnDemandSms Expenditure");
		return new ResponseEntity<Object>(expenditureData.add(yearExpenditure), HttpStatus.OK);
	}****/
	
	public ResponseEntity<Object> calculateYearCosts(){
		final EntityManager manager = factory.createEntityManager();
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		int firstMonthOfCurrentYear = 0;// 0 = january
		int lastMonthOfCurrentYear = 11;// 11 = december
		int firstDate = 1;
		
		int monthSmsExpenditure = 0;
		int monthScheduleExpenditure = 0;
		
		List<Integer> accruedMonthSmsExpenditure = new ArrayList<>();		
		List<Integer> accruedMonthScheduleExpenditure = new ArrayList<>();
		
		LineChartData yearSmsExpenditure = new LineChartData();
		LineChartData yearScheduleExpenditure = new LineChartData();
		
		List<LineChartData> expenditureData = new ArrayList<>();
		Calendar currentYear = Calendar.getInstance(TimeZone.getTimeZone("Africa/Kenya"));
		//iterate through the months of the year
		for(int month = firstMonthOfCurrentYear; month<=lastMonthOfCurrentYear; month++) {
			//set month starting with 0=january
			currentYear.set(Calendar.MONTH, month);
			//set DATE to 1st, so first date of that month
			currentYear.set(Calendar.DATE, firstDate);
			//get the date for the first day of that month
			Date firstDateOfMonth = currentYear.getTime();
			// set actual maximum date of that month
			currentYear.set(Calendar.DATE, currentYear.getActualMaximum(Calendar.DAY_OF_MONTH));
			//get the date for the last day of that month
			Date lastDateOfMonth = currentYear.getTime();
			monthSmsExpenditure = calculateOnDemandSmsCost(manager, builder, firstDateOfMonth, lastDateOfMonth, currentYear);
			monthScheduleExpenditure = calculateScheduleCost(manager, builder, firstDateOfMonth, lastDateOfMonth, currentYear);
			accruedMonthSmsExpenditure.add(monthSmsExpenditure);
			accruedMonthScheduleExpenditure.add(monthScheduleExpenditure);
		}
		String year = Integer.toString(currentYear.get(Calendar.YEAR));
		
		yearSmsExpenditure.setLabel(year+" OnDemandSms Expenditure");
		yearSmsExpenditure.setMonthlyExpenditure(accruedMonthSmsExpenditure);
		
		yearScheduleExpenditure.setLabel(year+" Schedules Expenditure");
		yearScheduleExpenditure.setMonthlyExpenditure(accruedMonthScheduleExpenditure);
		
		expenditureData.add(yearSmsExpenditure);
		expenditureData.add(yearScheduleExpenditure);
		
		return new ResponseEntity<Object>(expenditureData, HttpStatus.OK);
	}
	
	private int calculateOnDemandSmsCost(EntityManager manager, CriteriaBuilder builder, 
													Date firstDateOfMonth, Date lastDateOfMonth, Calendar currentYear){
		//initial month expenditure is zero
		int monthExpenditure = 0;
		CriteriaQuery<Sms> query = builder.createQuery(Sms.class);
		Root<Sms> root = query.from(Sms.class);
		Path<Date> date = root.get("date");
		Predicate predicate = builder.between(date, firstDateOfMonth, lastDateOfMonth);
		query.where(predicate);
		List<Sms> perMonthSmsList =  manager.createQuery(query).getResultList();
		//if not expenses for that month calculate the total expenditure
		if(perMonthSmsList != null) {
			for(Sms sms : perMonthSmsList) {
				monthExpenditure = monthExpenditure + sms.getCost();
			}
		}
		return monthExpenditure;
	}
	
	private int calculateScheduleCost(EntityManager manager, CriteriaBuilder builder, 
												Date firstDateOfMonth, Date lastDateOfMonth, Calendar currentYear){
		//initial month expenditure is zero
		int monthExpenditure = 0;
		//Query to get that months sms's between first and last dates
		CriteriaQuery<ScheduledSmsCost> query = builder.createQuery(ScheduledSmsCost.class);
		Root<ScheduledSmsCost> root = query.from(ScheduledSmsCost.class);
		Path<Date> date = root.get("dateSent");
		Predicate predicate = builder.between(date, firstDateOfMonth, lastDateOfMonth);
		query.where(predicate);
		List<ScheduledSmsCost> perMonthScheduleList =  manager.createQuery(query).getResultList();
		//if not expenses for that month calculate the total expenditure
		if(perMonthScheduleList != null) {
			for(ScheduledSmsCost schedule : perMonthScheduleList) {
				monthExpenditure = monthExpenditure + schedule.getCost();
			}
		}
		return monthExpenditure;
	}
}
