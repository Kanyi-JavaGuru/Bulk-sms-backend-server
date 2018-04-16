package co.ke.bigfootke.app.configs;

import java.text.ParseException;

import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

public class CronTriggerFactoryBeanConfig extends CronTriggerFactoryBean{

	public static final String JOB_DETAIL_KEY = "jobDetail";
	
    @Override
    public void afterPropertiesSet() throws ParseException{
        super.afterPropertiesSet();

        //Remove the JobDetail element
        getJobDataMap().remove(JOB_DETAIL_KEY);
    }
}
