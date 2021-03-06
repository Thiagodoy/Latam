package com.core.behavior.jobs;

import com.core.behavior.model.Agency;
import com.core.behavior.services.AgencyService;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.joda.time.LocalDateTime;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@DisallowConcurrentExecution
public class AgenciaFactoryJob extends QuartzJobBean {

    @Autowired
    private AgencyService agencyService;

    @Autowired
    private SchedulerFactoryBean bean;

    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {

        LocalDate now = LocalDate.now();
        DayOfWeek day = now.getDayOfWeek();

        if (day.equals(DayOfWeek.SATURDAY) || day.equals(DayOfWeek.SUNDAY)) {
            return;
        }

        List<Agency> agencias = agencyService
                .listAll()
                .stream()
                .filter(a -> a.getSendDailyUpload() != null && a.getSendDailyUpload().equals(1l))
                .collect(Collectors.toList());

        agencias.parallelStream().forEach(a -> {

            String jobName = a.getName();

            JobDetail detail = JobBuilder
                    .newJob(AgencyEmailJob.class)
                    .withIdentity("JOB-" + jobName + "-EMAIL", "send-email")
                    .usingJobData(AgencyEmailJob.JOB_KEY_AGENCIA, a.getId())
                    .build();

            Date dateStart = generateDate(a);
            SimpleTrigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity("trigger-" + jobName, "send-email")
                    .startAt(dateStart)
                    .withSchedule(simpleSchedule())
                    .build();

            try {
                bean.getScheduler().scheduleJob(detail, trigger);
            } catch (SchedulerException ex) {
                Logger.getLogger(AgenciaFactoryJob.class.getName()).log(Level.SEVERE, null, ex);
            }

        });

        Logger.getLogger(AgenciaFactoryJob.class.getName()).log(Level.INFO, "Agendamento realizado dos Jobs de envio de email");
    }

    private Date generateDate(Agency agency) {

        String[] time = agency.getTimeLimit().split(":");

        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);

        Long hourAdvace = agency.getHoursAdvance();

        LocalDateTime now = LocalDateTime.now();
        now = now.withHourOfDay(hour).withMinuteOfHour(minute).minusHours(hourAdvace.intValue());

        return now.toDate();

    }

}
