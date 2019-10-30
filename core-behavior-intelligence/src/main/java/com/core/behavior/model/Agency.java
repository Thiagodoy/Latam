package com.core.behavior.model;

import com.core.behavior.request.AgencyRequest;
import com.core.behavior.response.UserResponse;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Data
@Entity
@Table(schema = "behavior", name = "agency")
@NoArgsConstructor
@AllArgsConstructor
public class Agency {
    
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "input_path")
    private String inputPath;
    
    @Column(name = "processed_path")
    private String processedPath;
    
    @Column(name = "local_file_path")
    private String localFilePath;
    
    @Column(name = "agency_code")
    private String agencyCode;
    
    @Column(name = "od_flag")
    private Long odFlag;
    
    @Column(name = "flag_monthly")
    private String flagMonthly;
    
    @Column(name = "flag_approved")
    private Long flagApproved;
    
    @Column(name = "s3_path")
    private String s3Path;
    
    @Column(name = "cnpj")
    private String cnpj;
    
    @Column(name = "layout_file")
    private Long layoutFile;
    
    @Column(name = "send_email_daily_upload")
    private Long sendDailyUpload;   
    
    @Column(name = "hours_advance")
    private Long hoursAdvance;
    
    @Column(name = "time_limit")
    private String timeLimit;
    
    @Column(name = "profile")
    private String profile;
    
    @Column(name = "category")
    private String category;
    
    @Transient
    private List<UserResponse> users;
    
    
    public Agency(AgencyRequest request){
        this.name = request.getName();
        this.inputPath = request.getInputPath();
        this.processedPath = request.getProcessedPath();
        this.localFilePath = request.getLocalFilePath();
        this.agencyCode = request.getAgencyCode();
        this.odFlag = request.getOdFlag();
        this.flagMonthly = request.getFlagMonthly();
        this.flagApproved = request.getFlagApproved();
        this.s3Path = request.getS3Path();
        this.id = request.getId();
        this.cnpj = request.getCnpj();
        this.layoutFile = request.getLayoutFile();
        this.sendDailyUpload =  request.getSendDailyUpload();
        this.hoursAdvance = request.getHoursAdvance();
        this.timeLimit = request.getTimeLimit();
        this.category = request.getCategory();
        this.profile = request.getProfile();
    }

}
