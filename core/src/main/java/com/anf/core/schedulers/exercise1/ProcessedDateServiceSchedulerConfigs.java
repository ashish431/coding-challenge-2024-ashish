package com.anf.core.schedulers.exercise1;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;


@ObjectClassDefinition(name = "ProcessedDateServiceScheduler",
description = "Scheduler to trigger the ProcessedDate Service"
)
public @interface  ProcessedDateServiceSchedulerConfigs {
	
	
	@AttributeDefinition(
            name = "Enable the Scheduler?",
            description = "Check this to enable the scheduler",
            type = AttributeType.BOOLEAN)
    boolean schedulerEnabled() default true;
	
	@AttributeDefinition(
	            name = "Scheduler Name",
	            description = "Enter a unique identifier that represents name of the scheduler",
	            type = AttributeType.STRING
	    )
	String schedulerName() default "ProcessedDateServiceSchedulerConfigs";
	
	@AttributeDefinition(
		        name = "Cron job expression",
		        description = "Decides the frequency at which the schedular will run",
		        type = AttributeType.STRING)
	 String schedulerExpression() default "0 0/2 * * * ?";
}
