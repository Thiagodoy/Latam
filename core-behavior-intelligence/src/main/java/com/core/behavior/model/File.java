package com.core.behavior.model;

import com.core.behavior.util.StatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@Entity
@Table(schema = "behavior", name = "file")
@Data
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

   
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "company")
    private Long company;

    @Column(name = "qtd_total_lines")
    private Long qtdTotalLines;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusEnum status;    
    
    @Column(name = "repeated_lines")
    private Long repeatedLine;
    
    @Column(name = "execution_time")
    private Long executionTime;
    
    @Column(name = "parse_time")
    private Long parseTime;
    
    @Column(name = "persist_time")
    private Long persistTime;


    @Transient
    private List<FileProcessStatus> statusProcess;

}
