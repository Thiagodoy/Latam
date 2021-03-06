package com.core.behavior.configuration;

import com.core.behavior.aws.client.ClientAws;
import com.core.behavior.io.BeanIoReader;
import com.core.behavior.jobs.AgenciaFactoryJob;
import com.core.behavior.jobs.ConsumerEmailJob;
import com.core.behavior.jobs.FileReturnJob;
import com.core.behavior.jobs.IntegrationJob;
import com.core.behavior.jobs.ProcessFileJob;
import com.core.behavior.repository.AgencyRepository;
import com.core.behavior.repository.FileRepository;
import com.core.behavior.services.AgencyService;
import com.core.behavior.services.FileProcessStatusService;
import com.core.behavior.services.FileService;
import com.core.behavior.services.IntegrationService;
import com.core.behavior.services.LogService;
import com.core.behavior.services.NotificacaoService;
import com.core.behavior.services.SequenceService;
import com.core.behavior.services.TicketService;
import com.core.behavior.services.UserActivitiService;
import com.core.behavior.util.ThreadPoolFileIntegration;
import com.core.behavior.util.ThreadPoolFileReturn;
import com.core.behavior.util.ThreadPoolFileValidation;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Configuration
@EnableAutoConfiguration
public class QuartzConfiguration {

    public QuartzConfiguration(SchedulerFactoryBean bean) throws SchedulerException {

        Scheduler scheduler = bean.getScheduler();       

        scheduler.start();

        JobDetail detail = JobBuilder.newJob(ConsumerEmailJob.class).withIdentity("ConsumerEmailJob", "consumer-email")
                .withDescription("Sender email")
                .build();
        CronTrigger crontrigger = TriggerBuilder.newTrigger().withIdentity("ConsumerEmailJobTrigger", "consumer-email")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/3 * 1/1 * ? *")
                        .withMisfireHandlingInstructionFireAndProceed())
                .build();
        scheduler.scheduleJob(detail, crontrigger);

        JobDetail detail1 = JobBuilder.newJob(AgenciaFactoryJob.class).withIdentity("AgenciaFactoryJob", "agencia-factory-email")
                .withDescription("Agendador de notificação de email")
                .build();
        CronTrigger crontrigger1 = TriggerBuilder.newTrigger().withIdentity("AgenciaFactoryJob", "agencia-factory-email")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 1/1 * ? *")
                        .withMisfireHandlingInstructionFireAndProceed())
                .build();

        scheduler.scheduleJob(detail1, crontrigger1);

     

    }

    @Bean
    @Scope("singleton")
    public ThreadPoolFileReturn threadPoolExecutor() {
        return new ThreadPoolFileReturn();
        
    }

    @Bean
    @Scope("singleton")
    public ThreadPoolFileValidation threadPoolFileValidation() {
        return new ThreadPoolFileValidation();
        
    }

    @Bean
    @Scope("singleton")
    public ThreadPoolFileIntegration threadPoolFileIntegration() {
        return new ThreadPoolFileIntegration();
        
    }

    @Bean
    public IntegrationJob integrationJob(TicketService ticketService, FileService fileService, IntegrationService integrationService ) {
        return new IntegrationJob(ticketService, fileService, integrationService);
    }   
    
    @Bean
    public FileReturnJob fileReturnJob1(ClientAws clientAws, UserActivitiService userActivitiService, AgencyRepository agencyRepository, FileRepository fileRepository, NotificacaoService notificacaoService) {
        return new FileReturnJob(clientAws, userActivitiService, agencyRepository, fileRepository, notificacaoService);
    }

    @Bean
    public ProcessFileJob processFileJob1(LogService logService, AgencyService agencyService, FileService fileService,
            FileProcessStatusService fileProcessStatusService, TicketService ticketService, SequenceService sequenceService) {
        return new ProcessFileJob(new BeanIoReader(fileService), logService, agencyService, fileService, fileProcessStatusService, ticketService, sequenceService);
    }

}
