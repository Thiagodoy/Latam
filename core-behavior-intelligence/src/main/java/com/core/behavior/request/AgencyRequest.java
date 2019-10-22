package com.core.behavior.request;

import javax.persistence.Column;
import lombok.Data;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
public class AgencyRequest {

    private Long id;

    private String name;

    private String inputPath;

    private String processedPath;

    private String localFilePath;

    private String agencyCode;

    private Long odFlag;
    private String flagMonthly;

    private Long flagApproved;    
    
    private Long layoutFile;
    
    
    private String s3Path;
    
    private String cnpj;
    
    private Long sendDailyUpload;   
    
    
    private Long hoursAdvance;
    
    
    private String timeLimit;
    
    private String category;
    
    private String profile;

}
