package com.core.behavior.model;

import com.core.behavior.dto.ConsolidateOrCompanyDTO;
import com.core.behavior.dto.DayStatusDTO;
import com.core.behavior.dto.FrequencyStatusDTO;
import com.core.behavior.dto.ResultRules2;
import com.core.behavior.request.AgencyRequest;
import com.core.behavior.response.UserResponse;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */



@SqlResultSetMapping(name = "Rules2", classes = @ConstructorResult(
        targetClass = ResultRules2.class,
        columns = {
            @ColumnResult(name = "reg", type = Long.class)
            ,                    
                    @ColumnResult(name = "consolidada", type = Long.class)
            ,
                    @ColumnResult(name = "empresa", type = Long.class)
        }))

@SqlResultSetMapping(name = "ConsolidateOrCompany", classes = @ConstructorResult(
        targetClass = ConsolidateOrCompanyDTO.class,
        columns = {
            @ColumnResult(name = "consolidada", type = Long.class)
            ,                    
                    @ColumnResult(name = "empresa", type = Long.class),}))



@SqlResultSetMapping(name = "Frequency", classes = @ConstructorResult(
        targetClass = FrequencyStatusDTO.class,
        columns = {
                    @ColumnResult(name = "data", type = LocalDate.class),                    
                    @ColumnResult(name = "week", type = Long.class),                    
                    @ColumnResult(name = "value", type = Long.class),}))

@SqlResultSetMapping(name = "DayStatus", classes = @ConstructorResult(
        targetClass = DayStatusDTO.class,
        columns = {
                    @ColumnResult(name = "information", type = Long.class),                    
                    @ColumnResult(name = "frequency", type = Long.class),
                    @ColumnResult(name = "quality", type = Double.class)}))

@NamedNativeQuery(name = "Agency.checkDayStatus", query = "select (select sign(count(1)) from behavior.ticket where date(data_emissao) = :data and code_agency= :codeAgency) as 'information',\n"
        + "(select sign(count(1)) from  behavior.file where date(created_at) = :data and  version = 1 and company = :idCompany) as 'frequency',\n"
        + "IFNULL((select q from (select a.id,  (count(b.id)/a.qtd_total_lines) as q from behavior.file a left join behavior.ticket b on a.id = b.file_id where a.version = 1 and company = :idCompany and date(a.created_at) = :data and b.status = 'WRITED' group by a.id order by a.id desc limit 1) as x),0) as 'quality'",
        resultSetMapping = "DayStatus")


@NamedNativeQuery(name = "Agency.checkConsolidateOrCompanyStatus", query = "select ifnull(sum(consolidada),0) as consolidada, ifnull(sum(empresa),0) as empresa from (select  data_emissao, sign(count(consolidada)) as consolidada, sign(count(empresa)) as empresa from behavior.ticket t  where t.data_emissao between :start and  :end and code_agency = :code_agency and weekday(t.data_emissao) not in (5,6) and not exists(select 1 from behavior.holiday h where h.holiday_date = t.data_emissao)  group by  data_emissao) a",
        resultSetMapping = "ConsolidateOrCompany")

@NamedNativeQuery(
        name = "Agency.checkRules2",
        query = "select count(*) as reg, count(b.consolidada) as consolidada, count(b.empresa) as empresa from behavior.file a inner join behavior.ticket b on a.id = b.file_id where a.created_at between :dateStart and :dateEnd and a.company = :agencia",
        resultSetMapping = "Rules2")

@NamedNativeQuery( 
        name = "Agency.checkStatusFrequency",
        query = "select t.data_emissao as data, weekofyear(t.data_emissao) as week, sign(sum(t.data_emissao)) as value  from behavior.ticket t where t.code_agency = :code and  date(t.data_emissao) between :start and :end and weekday(t.data_emissao) not in (5,6) group by t.data_emissao",
        resultSetMapping = "Frequency")


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
