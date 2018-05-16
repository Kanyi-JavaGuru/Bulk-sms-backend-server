package co.ke.bigfootke.app.quartz.services;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TriggerListener implements org.quartz.TriggerListener {

	private static final Logger log = LoggerFactory.getLogger(TriggerListener.class);

	@Override
    public String getName() {
        return "globalTrigger";
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
		log.info("***** Trigger fired for sms schedule: "+context.getJobDetail().getKey());
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
		log.info("***** WARNING: Sms schedule: "+trigger.getJobKey().getName()+" misfired");
        
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, CompletedExecutionInstruction triggerInstructionCode) {
		log.info("***** Trigger for sms schedule: "+context.getJobDetail().getKey()+" Completed");
    }

}
