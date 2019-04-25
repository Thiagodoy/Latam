package com.core.behavior.model;

import com.core.behavior.request.AgencyRequest;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
    }

}
