package com.core.behavior.configuration;

import com.core.behavior.quartz.listenner.BehaviorJobListenner;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.quartz.impl.matchers.GroupMatcher;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Configuration
public class QuartzConfiguration {

    public QuartzConfiguration(SchedulerFactoryBean bean) throws SchedulerException {

        Scheduler scheduler = bean.getScheduler();

        scheduler.getListenerManager().addJobListener(new BehaviorJobListenner(), GroupMatcher.jobGroupEquals("fg_jobgroup_01"));

        scheduler.start();

    }

}
