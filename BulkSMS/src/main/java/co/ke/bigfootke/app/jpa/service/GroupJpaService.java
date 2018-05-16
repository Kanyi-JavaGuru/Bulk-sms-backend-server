package co.ke.bigfootke.app.jpa.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import co.ke.bigfootke.app.jpa.entities.Group;
import co.ke.bigfootke.app.jpa.entities.ScheduledSmsCost;
import co.ke.bigfootke.app.jpa.entities.Sms;
import co.ke.bigfootke.app.jpa.implementations.ClientJpaImplementation;
import co.ke.bigfootke.app.jpa.implementations.GroupJpaImplementation;
import co.ke.bigfootke.app.jpa.implementations.ScheduleJpaImplementation;
import co.ke.bigfootke.app.jpa.implementations.SmsJpaImplementation;
import co.ke.bigfootke.app.pojos.Report;

@Service
public class GroupJpaService {

	@Autowired
	private GroupJpaImplementation repository;
	@Autowired
	private ClientJpaImplementation clientRepo;
	@Autowired
	private SmsJpaImplementation smsRepo;
	@Autowired
	private	ScheduleJpaImplementation scheduleRepo;
	
	private Map<String, String> response;
	
	private final static int FIRST_DAY = 1;
	private static final Logger log = LoggerFactory.getLogger(GroupJpaService.class);
	private Object lockSmsMonth = new Object();
	private int[] totalSmsExpense = new int[12];
	private Object lockScheduleMonth = new Object();
	private int[] totalScheduleExpense = new int[12];
		
	public ResponseEntity<Object> create(Group group) {
		response = new HashMap<>();
		if(group.getName() != null && !(repository.findByName(group.getName()) != null)) {
			Group newGroup = repository.create(group);
			response.put("message", "Success: Created "+newGroup);
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}
		response.put("message", "Error: Group may already exist or Group Name not included");
		return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
	}
	
	public ResponseEntity<Object> findById(Long groupId) {
		if(repository.exists(groupId)) {
			return new ResponseEntity<Object>(repository.findById(groupId), HttpStatus.OK);
		}
		response = new HashMap<>();
		response.put("message", "Error: Group not found");
		return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
	}
	
	public ResponseEntity<Object> findByName(String groupName) {
		if(repository.findByName(groupName) != null) {
			return new ResponseEntity<Object>(repository.findByName(groupName), HttpStatus.OK);
		}
		response = new HashMap<>();
		response.put("message", "Error: Group not found");
		return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
	}
	
	public ResponseEntity<Object> findAll() {
		return new ResponseEntity<Object>(repository.findAll(), HttpStatus.OK);
	}	
	
	public ResponseEntity<Object> update(Group group) {
		response = new HashMap<>();
		if(repository.exists(group.getGroupId())) {
			return new ResponseEntity<Object>(repository.update(group), HttpStatus.OK);
		}			
		response.put("message", "Error: Could not find Group");
		return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
	}
	
	public ResponseEntity<Object> delete(Long groupId) {
		response = new HashMap<>();
		if(repository.exists(groupId)) {
			response.put("message", "Deleted");
			repository.delete(groupId);
			return new ResponseEntity<Object>(response, HttpStatus.OK);				
		}
		response.put("message", "Error: Client not found");
		return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
	}

	public ResponseEntity<Object> addToOnDemandSms(Long smsId, List<Long> groupIds) {
		repository.addToOnDemandSms(smsId, groupIds);
		response = new HashMap<>();
		response.put("message", "Error: Groups have been added");
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}	

	public ResponseEntity<Object> addToSchedule(Long scheduleId, List<Long> groupIds) {
		repository.addToSchedule(scheduleId, groupIds);
		response = new HashMap<>();
		response.put("message", "Error: Groups have been added");
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
	
	public ResponseEntity<Object> deleteFromSchedule(Long smsId, Long groupId) {
		return new ResponseEntity<Object>(repository.deleteFromSchedule(smsId, groupId), HttpStatus.OK);
	}

	public ResponseEntity<Object> calculateGroupCosts(List<Long> groupIds) {		
		return new ResponseEntity<Object>(repository.calculateCosts(groupIds), HttpStatus.OK);
	}
	
	public ResponseEntity<Object> getMonthlyExpense(int year){
		ExecutorService service = Executors.newFixedThreadPool(4);
		Report[] expenseReports = new Report[2];
		String smsReportLabel = "SMS Expense";
		String scheduleReportLabel = "Campaigns Expenses";
		for(int month = 0; month<=11; month++) {
			final int currentMonth = month;
			service.execute(new Runnable() {				
				@Override
				public void run() {
					log.info("*** Month "+(currentMonth+1));
					addSmsExpenseToExpenses(currentMonth, calculateSmsExpenses(getSmsCostList(currentMonth)));
				}
			});
			service.execute(new Runnable() {				
				@Override
				public void run() {
					log.info("*** Month "+(currentMonth+1));
					addScheduleExpenseToExpenses(currentMonth, calculateScheduleExpenses(getScheduleCostList(currentMonth)));
				}
			});
		}
		//shutdown pool when all threads are completed
		service.shutdown();
		//block the main thread for sometime or until threads in pool are done
		try {
			service.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		expenseReports[0] = prepareReport(smsReportLabel, totalSmsExpense);
		expenseReports[1] = prepareReport(scheduleReportLabel, totalScheduleExpense);
		return new ResponseEntity<Object>(expenseReports, HttpStatus.OK);
	}
		
	/*** to showcase CompletableFuture
//	@Async
//	private void getScheduleExpensesAsync(int month) {
//
//		CompletableFuture.supplyAsync(()->getScheduleCostList(month))
//		.thenApply(expenseList -> calculateScheduleExpenses(expenseList, month))
//		.thenAccept(expense -> addScheduleExpenseToExpenses(month, expense));
//	}
 */
	
	private List<Sms> getSmsCostList(int month){
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Africa/Kenya"));
		Date first_day = getFirstDayOfMonth(calendar, month);
		Date last_day = getLastDayOfMonth(calendar);
		//retrieve expenses of that month from database
		return smsRepo.findBtwnDates(first_day, last_day);
	}
	
	private List<ScheduledSmsCost> getScheduleCostList(int month){
//		log.info("*** Accessing database for schedule list for Month: " +(month+1));
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Africa/Kenya"));
		Date first_day = getFirstDayOfMonth(calendar, month);
		Date last_day = getLastDayOfMonth(calendar);
		//retrieve expenses of that month from database
		return scheduleRepo.findCostBtwnDates(first_day, last_day);
	}
	
	private Date getFirstDayOfMonth(Calendar calendar, int month) {
		//set month starting with 0=january
		calendar.set(Calendar.MONTH, month);
		//set DATE to 1st, so first date of that month
		calendar.set(Calendar.DATE, FIRST_DAY);
		//get the date for the first day of that month
		return calendar.getTime();
	}
	private Date getLastDayOfMonth(Calendar calendar) {
		// set actual maximum date of that month
		calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		//get the date for the last day of that month
		return calendar.getTime();
	}
	
	private int calculateSmsExpenses(List<Sms> smsExpensesList) {
		int totalMonthExpense = 0;
		if(smsExpensesList != null) {
			for(Sms smsExpense : smsExpensesList) {
				totalMonthExpense = totalMonthExpense + smsExpense.getCost();
			}
		}
		return totalMonthExpense;
	}
	
	private int calculateScheduleExpenses(List<ScheduledSmsCost> expenseList) {
		int totalMonthExpense = 0;
		if(expenseList != null) {
			for(ScheduledSmsCost expense : expenseList) {
				totalMonthExpense = totalMonthExpense + expense.getCost();
			}
		}
		return totalMonthExpense;
	}
	
	private void addSmsExpenseToExpenses(int month, int expense) {
		synchronized(lockSmsMonth) {
			totalSmsExpense[month] = expense;
		}
	}
	
	private void addScheduleExpenseToExpenses(int month, int expense) {
		synchronized(lockScheduleMonth) {
			totalScheduleExpense[month] = expense;
		}
	}
	
	private Report prepareReport(String reportLabel, int[] monthlyExpense) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Africa/Kenya"));
		String financialYear = Integer.toString(calendar.get(Calendar.YEAR));
		return new Report(reportLabel+": " +financialYear, monthlyExpense);
	}
	
	public String processPhoneNos(List<Long> groupIds) {
		List<Long> processedGroups = new ArrayList<>();
		for(Long groupId : groupIds) {
			if(repository.exists(groupId) && groupId > 0) {
				processedGroups.add(groupId);
			}
		}
		return clientRepo.processPhoneNos(processedGroups);
	}	
}
