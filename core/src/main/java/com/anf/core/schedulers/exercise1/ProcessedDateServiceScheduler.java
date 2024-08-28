package com.anf.core.schedulers.exercise1;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anf.core.services.exercise1.ProcessedDateService;

import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;

@Component(service = ProcessedDateServiceScheduler.class,immediate=true)
@Designate(ocd = ProcessedDateServiceSchedulerConfigs.class)
public class ProcessedDateServiceScheduler implements Runnable{

	private static final Logger LOG = LoggerFactory.getLogger(ProcessedDateServiceScheduler.class);
	
	boolean schedulerEnabled;
	String schedulerName;
	String cronExpression;
	
	@Reference 
	private ProcessedDateService processedDateService;
	
	@Reference
	private Scheduler scheduler;
	
    @Activate
    protected void activate(ProcessedDateServiceSchedulerConfigs config) {

        LOG.error(" PracticeScheduledTask activate method called");
        this.schedulerEnabled = config.schedulerEnabled();
        this.schedulerName = config.schedulerName();
        this.cronExpression = config.schedulerExpression();
        addScheduler();

    }

    @Deactivate
    protected void deactivate() {
        removeScheduler();
    }


    
    @Modified
    protected void modified(ProcessedDateServiceSchedulerConfigs configuration) {
        // Remove the scheduler registered with old configuration
        removeScheduler();
        // Add the scheduler registered with new configuration
        addScheduler();
    }
    
    
    
    private void addScheduler() {
        // 
        if (schedulerEnabled) {
            // Adding scheduler options
        	ScheduleOptions scheduleOptions = scheduler.EXPR(cronExpression);
            scheduleOptions.canRunConcurrently(false);
            scheduler.schedule(this, scheduleOptions);
            LOG.error("Scheduler added successfully name='{}'", schedulerName);
        } else 
            removeScheduler();
        
    }

    //method to remove the scheduler
    private void removeScheduler() {
        scheduler.unschedule(schedulerName);
    }
    

	@Override
	public void run() {
		LOG.error("#### -> Called <- ####");
		processedDateService.processDate();
	}
	
}
