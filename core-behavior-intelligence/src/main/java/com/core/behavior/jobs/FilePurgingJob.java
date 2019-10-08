/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.behavior.jobs;

import com.core.behavior.util.Constantes;
import com.core.behavior.util.Utils;

import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *
 * @author thiag
 */
@DisallowConcurrentExecution
public class FilePurgingJob extends QuartzJobBean {

    private final int DATA_LIMITE = 30;

    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {

        File uploadedFolder = new File(Constantes.DIR_UPLOADED);

        List<File> files = Arrays.asList(uploadedFolder.listFiles());

        LocalDate limitDate = LocalDate.now().minus(DATA_LIMITE, ChronoUnit.DAYS);

        files.parallelStream().filter(f -> {

            final LocalDate lastModification = Utils.dateToLocalDate(new Date(f.lastModified()));
            return limitDate.isAfter(lastModification);

        }).forEach(f -> {

            try {
                FileUtils.forceDelete(f);
            } catch (Exception e) {
                Logger.getLogger(FilePurgingJob.class.getName()).log(Level.WARNING, " file -> " + f.getName(), e);
            }

        });

    }

}
