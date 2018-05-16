package co.ke.bigfootke.app.quartz.controllers;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.ke.bigfootke.app.quartz.jobs.CronJob;
import co.ke.bigfootke.app.quartz.jobs.SimpleJob;
import co.ke.bigfootke.app.quartz.services.JobService;

@RestController
@RequestMapping(value = "api/scheduler/")
public class JobController {

	@Autowired
	@Lazy
	private JobService jobService;
	private static final Logger log = LoggerFactory.getLogger(JobController.class);

	@RequestMapping("schedule")	
	public void schedule(@RequestParam("jobName") String jobName, 
						@RequestParam("jobScheduleTime") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") 
														Date jobScheduleTime, 
						@RequestParam("cronExpression") String cronExpression){
		//Job Name is mandatory
		if(jobName == null || jobName.trim().equals("")){
			log.info("***** ERROR: Can not create sms schedule without a name");
		}
		//Check if job Name is unique;
		if(!jobService.isJobWithNamePresent(jobName)){

			if(cronExpression == null || cronExpression.trim().equals("")){
				log.info("***** Creating sms schedule: "+jobName+" as Simple Job");
				//Single Trigger
				jobService.scheduleOneTimeJob(jobName, SimpleJob.class, jobScheduleTime);
				
			}else{
				log.info("***** Creating sms schedule: "+jobName+" as Cron Job");
				//Cron Trigger
				jobService.scheduleCronJob(jobName, CronJob.class, jobScheduleTime, cronExpression);
								
			}
		}
	}

	@RequestMapping("unschedule")
	public String unschedule(@RequestParam("jobName") String jobName) {
		log.info("***** Unscheduling sms schedule: "+jobName);
		boolean status = jobService.unScheduleJob(jobName);
		if(status)
			return "job "+jobName+" has been unscheduled";
		return "job "+jobName+" has not been unscheduled";
	}

	@RequestMapping("delete")
	public String delete(@RequestParam("jobName") String jobName) {
		log.info("***** Deleting sms schedule: "+jobName);	
		if(jobService.isJobWithNamePresent(jobName)){
			boolean isJobRunning = jobService.isJobRunning(jobName);
			if(!isJobRunning){
				jobService.deleteJob(jobName);
			}
		}
		return "job "+jobName+" has been deleted";
	}

	@RequestMapping("pause")
	public String pause(@RequestParam("jobName") String jobName) {
		log.info("***** Pausing sms schedule: "+jobName);	
		if(jobService.isJobWithNamePresent(jobName)){

			boolean isJobRunning = jobService.isJobRunning(jobName);

			if(!isJobRunning) {
				jobService.pauseJob(jobName);
			}
		}
		return "job "+jobName+" has been stopped";
	}

	@RequestMapping("resume")
	public String resume(@RequestParam("jobName") String jobName) {	
		if(jobService.isJobWithNamePresent(jobName)){
			String jobState = jobService.getJobState(jobName);

			if(jobState.equals("PAUSED")){
				log.info("***** Resuming paused sms schedule: "+jobName);
				jobService.resumeJob(jobName);
			}
		}
		return "job "+jobName+" has been paused";
	}

	@RequestMapping("update")
	public String updateJob(@RequestParam("jobName") String jobName, 
			@RequestParam("jobScheduleTime") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") Date jobScheduleTime, 
			@RequestParam("cronExpression") String cronExpression){
		log.info("***** Updating schedule: "+jobName);
		//Job Name is mandatory
		if(jobName == null || jobName.trim().equals("")){
			log.info("***** ERROR: Can not update without sms schedule name");
		}

		//Edit Job
		if(jobService.isJobWithNamePresent(jobName)){
			
			if(cronExpression == null || cronExpression.trim().equals("")){
				//Single Trigger
				jobService.updateOneTimeJob(jobName, jobScheduleTime);
				return "Please enter a jobname.";				
			}else{
				//Cron Trigger
				jobService.updateCronJob(jobName, jobScheduleTime, cronExpression);
				return "job "+jobName+" successfully updated";						
			}
		}
		return "job "+jobName+" does exist";
	}

	@RequestMapping("jobs")
	public List<Map<String, Object>> getAllJobs(){
		log.info("***** Retrieving all sms schedules");
		List<Map<String, Object>> list = jobService.getAllJobs();
		return list;
	}

	@RequestMapping("checkJobName")
	public String checkJobName(@RequestParam("jobName") String jobName){
		System.out.println("JobController.checkJobName()");
		//Job Name is mandatory
		if(jobName == null || jobName.trim().equals("")){
			return "Please enter a jobname.";
		}		
		boolean status = jobService.isJobWithNamePresent(jobName);
		if(status) {
			return "job "+jobName+" does exist";
		}
		return "job "+jobName+" does not exist";
	}

	@RequestMapping("isJobRunning")
	public boolean isJobRunning(@RequestParam("jobName") String jobName) {
		log.info("***** Checking running status of sms schedule: "+jobName);
		boolean status = jobService.isJobRunning(jobName);
		return status;
	}

	@RequestMapping("jobState")
	public String getJobState(@RequestParam("jobName") String jobName) {
		log.info("***** Checking state of sms schedule: "+jobName);
		String jobState = jobService.getJobState(jobName);		
		return jobState;
	}

	@RequestMapping("stop")
	public String stopJob(@RequestParam("jobName") String jobName) {
		if(jobService.isJobWithNamePresent(jobName)){
			if(jobService.isJobRunning(jobName)){
				jobService.stopJob(jobName);
				log.info("***** Stopped sms schedule: "+jobName);
				return "job "+jobName+" successfully stopped";
			}
			return "job "+jobName+" was not running";
		}
		return "job "+jobName+" does not exist";
	}

	@RequestMapping("start")
	public String startJobNow(@RequestParam("jobName") String jobName) {
		if(jobService.isJobWithNamePresent(jobName)){

			if(!jobService.isJobRunning(jobName)){
				jobService.startJobNow(jobName);
				log.info("***** Started sms schedule: "+jobName);
				return "job "+jobName+" successfully started";
			}
			return "job "+jobName+" was already running";
		}
		return "job "+jobName+" does not exist";
	}

}
