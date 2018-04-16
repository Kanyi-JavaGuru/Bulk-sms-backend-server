package co.ke.bigfootke.app.quartz.controllers;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.ke.bigfootke.app.quartz.jobs.CronJob;
import co.ke.bigfootke.app.quartz.jobs.SimpleJob;
import co.ke.bigfootke.app.quartz.services.JobService;

@RestController
@RequestMapping(value = "api/scheduler")
@CrossOrigin(origins="http://localhost:4200")
public class JobController {

	@Autowired
	@Lazy
	JobService jobService;

	@RequestMapping("schedule")	
	public void schedule(@RequestParam("jobName") String jobName, 
			@RequestParam("jobScheduleTime") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") Date jobScheduleTime, 
			@RequestParam("cronExpression") String cronExpression){
		System.out.println("JobController.schedule()");

		//Job Name is mandatory
		if(jobName == null || jobName.trim().equals("")){
		}

		//Check if job Name is unique;
		if(!jobService.isJobWithNamePresent(jobName)){

			if(cronExpression == null || cronExpression.trim().equals("")){
				//Single Trigger
				jobService.scheduleOneTimeJob(jobName, SimpleJob.class, jobScheduleTime);
				
			}else{
				//Cron Trigger
				jobService.scheduleCronJob(jobName, CronJob.class, jobScheduleTime, cronExpression);
								
			}
		}
	}

	@RequestMapping("unschedule")
	public String unschedule(@RequestParam("jobName") String jobName) {
		System.out.println("JobController.unschedule()");
		boolean status = jobService.unScheduleJob(jobName);
		if(status)
			return "job "+jobName+" has been unscheduled";
		return "job "+jobName+" has not been unscheduled";
	}

	@RequestMapping("delete")
	public String delete(@RequestParam("jobName") String jobName) {
		System.out.println("JobController.delete()");		

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
		System.out.println("JobController.pause()");

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
		System.out.println("JobController.resume()");

		if(jobService.isJobWithNamePresent(jobName)){
			String jobState = jobService.getJobState(jobName);

			if(jobState.equals("PAUSED")){
				System.out.println("Job current state is PAUSED, Resuming job...");
				jobService.resumeJob(jobName);
			}
		}
		return "job "+jobName+" has been paused";
	}

	@RequestMapping("update")
	public String updateJob(@RequestParam("jobName") String jobName, 
			@RequestParam("jobScheduleTime") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm") Date jobScheduleTime, 
			@RequestParam("cronExpression") String cronExpression){
		System.out.println("JobController.updateJob()");

		//Job Name is mandatory
		if(jobName == null || jobName.trim().equals("")){
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
		System.out.println("JobController.getAllJobs()");
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
		System.out.println("JobController.isJobRunning()");

		boolean status = jobService.isJobRunning(jobName);
		return status;
	}

	@RequestMapping("jobState")
	public String getJobState(@RequestParam("jobName") String jobName) {
		System.out.println("JobController.getJobState()");

		String jobState = jobService.getJobState(jobName);
		
		return jobState;
	}

	@RequestMapping("stop")
	public String stopJob(@RequestParam("jobName") String jobName) {
		System.out.println("JobController.stopJob()");

		if(jobService.isJobWithNamePresent(jobName)){

			if(jobService.isJobRunning(jobName)){
				jobService.stopJob(jobName);
				return "job "+jobName+" successfully stopped";
			}

			return "job "+jobName+" was not running";
		}

		return "job "+jobName+" does not exist";
	}

	@RequestMapping("start")
	public String startJobNow(@RequestParam("jobName") String jobName) {
		System.out.println("JobController.startJobNow()");

		if(jobService.isJobWithNamePresent(jobName)){

			if(!jobService.isJobRunning(jobName)){
				jobService.startJobNow(jobName);
				return "job "+jobName+" successfully started";
			}
			return "job "+jobName+" was already running";
		}
		return "job "+jobName+" does not exist";
	}

}
