package com.core.behavior.model;

import com.core.behavior.dto.FileStatusProcessDTO;
import com.core.behavior.dto.FileLinesApprovedDTO;
import com.core.behavior.util.StatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
@SqlResultSetMapping(name = "FileStatusProcess",
        classes = @ConstructorResult(
                targetClass = FileStatusProcessDTO.class,
                columns = {
                    @ColumnResult(name = "date", type = Date.class)
                    ,                    
                    @ColumnResult(name = "qtd", type = BigInteger.class)
                    ,
                    @ColumnResult(name = "status", type = String.class)

                }))

@SqlResultSetMapping(name = "moveToAnalitic",
        classes = @ConstructorResult(
                targetClass = FileLinesApprovedDTO.class,
                columns = {
                    @ColumnResult(name = "file_id", type = Long.class)
                    ,                    
                    @ColumnResult(name = "qtd", type = Long.class),}))

@NamedNativeQuery(name = "File.statusProcesss", query = "SELECT *\n"
        + "FROM   (SELECT Date(created_at) AS date,\n"
        + "               Count(1) as qtd,\n"
        + "               status\n"
        + "        FROM   behavior.file f where company = :agencia and created_at between :start and :end \n"
        + "        GROUP  BY Date(created_at),\n"
        + "                  status) rr\n"
        + "ORDER  BY date ASC", resultSetMapping = "FileStatusProcess")

@NamedNativeQuery(name = "File.moveToAnalitics", query = "SELECT file_id, \n"
        + "       Count(*) AS qtd \n"
        + "FROM   behavior.ticket a \n"
        + "       INNER JOIN behavior.file b \n"
        + "               ON a.file_id = b.id \n"
        + "WHERE b.status = 'VALIDATION_SUCCESS' \n"
        + "       AND a.status = 'APPROVED' \n"
        + "       AND b.id = :file  \n"        
        + "GROUP  BY file_id",
        resultSetMapping = "moveToAnalitic")

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

    @Column(name = "company", unique = true)
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

    @Column(name = "validation_time")
    private Long validationTime;

    @Column(name = "stage")
    private Long stage;

    @Column(name = "version")
    private Long version;

    @Transient
    private List<FileProcessStatus> statusProcess;

}
