package com.core.behavior.configuration;

import com.core.behavior.jobs.ProcessFileJob;
import com.core.behavior.quartz.listenner.BehaviorJobListenner;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Matcher;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Configuration
public class QuartzConfiguration {

    public QuartzConfiguration(SchedulerFactoryBean bean) throws SchedulerException {

        Scheduler scheduler = bean.getScheduler();

        scheduler.getListenerManager().addJobListener(new BehaviorJobListenner(), (t) -> {
            return t.getGroup().equals("process-file");
        });

        scheduler.start();

    }

}
