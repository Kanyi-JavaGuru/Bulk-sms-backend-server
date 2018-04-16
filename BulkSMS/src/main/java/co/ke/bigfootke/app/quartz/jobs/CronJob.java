package co.ke.bigfootke.app.quartz.jobs;

import java.util.Date;

import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import co.ke.bigfootke.app.africastalking.SmsDispatcher;
import co.ke.bigfootke.app.jpa.entities.ScheduledSms;
import co.ke.bigfootke.app.jpa.implementations.ScheduleJpaImplementation;
import co.ke.bigfootke.app.jpa.service.SmsJpaService;
import co.ke.bigfootke.app.quartz.services.JobService;

public class CronJob extends QuartzJobBean implements InterruptableJob{

@SuppressWarnings("unused")
private volatile boolean toStopFlag = true;
	
	@Autowired
	JobService jobService;
	@Autowired
	private ScheduleJpaImplementation scheduledSmsImpl;
	@Autowired
	private SmsJpaService smsService;
	private SmsDispatcher dispatcher = new SmsDispatcher();
	private static final Logger log = LoggerFactory.getLogger(ScheduleJpaImplementation.class);
	
	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobKey key = jobExecutionContext.getJobDetail().getKey();
		log.info("***** Cron Job started with key:"+ key.getName() + ", Group :"+key.getGroup() + " , Thread Name :"+Thread.currentThread().getName() + " ,Time now :"+new Date());
		//retrieve the job name which is similar to scheduledSms title
		String smsTitle = key.getName();
		//retrieve the scheduledSms 
		ScheduledSms scheduledSms = scheduledSmsImpl.findByTitle(smsTitle);
		if(scheduledSms != null) {
			log.info("***** Cron Job retrieving phoneNos");
			//retrieve groups of scheduled sms and process them to get the phoneNos
			String recipients = smsService.processGroups(scheduledSms.getGroups());
			if(recipients != null)
				//dispatch sms to recipients/phoneNos
				dispatcher.sendOnDemandSms(recipients, scheduledSms.getMessage());
		}
		log.info("***** Cron Job could not find schedule: Cron Job stopped.");
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		log.info("***** An INTERRUPT occurred: Cron Job stopped.");
		toStopFlag = false;
	}
}
